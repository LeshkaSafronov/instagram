import base64
import io

import aiopg
import aiobotocore
import aiojobs

import configs
import style_transfer_nn
import vgg19_model_loader

from aiohttp import web
from aiohttp.web_app import Application
from PIL import Image


__all__ = ['Server']


class Server(Application):
    """
    Application server implementation,
    inherito from aiohttp.Application.
    More about aiohttp: https://aiohttp.readthedocs.io/en/stable/
    """
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.db_pool = None
        self.scheduler = None
        self.s3_session = None
        self.on_cleanup.append(self.on_server_shutdown)

    async def _get_key_from_db(self, table_name, item_id):
        """
        loading image key from database,
        More about aiopg: https://github.com/aio-libs/aiopg

        :param table_name (str): table name on database
        :param item_id (str): image key on database
        """
        async with self.db_pool.acquire() as conn:
            async with conn.cursor() as cur:
                await cur.execute(f'SELECT key from {table_name} WHERE id = {item_id}')
                return (await cur.fetchone())[0]

    async def _mark_photo_as_ready(self, photo_id):
        async with self.db_pool.acquire() as conn:
            async with conn.cursor() as cur:
                await cur.execute(f'UPDATE photos SET is_ready = TRUE WHERE id = {photo_id}')

    async def _get_image_from_s3(self, basket_name, file_key):
        """
        Loading image from minio s3 server,
        More about aiobotocore: https://github.com/aio-libs/aiobotocore

        :param basket_name (str): basket name on minio server
        :param file_key (str): file name on minio server
        """
        async with self.s3_session.create_client(service_name='s3',
                                                 aws_secret_access_key=configs.SECRET_KEY,
                                                 aws_access_key_id=configs.ACCESS_KEY,
                                                 endpoint_url=configs.MINIO_URL) as client:
            response = await client.get_object(Bucket=basket_name, Key=file_key)
            async with response['Body'] as stream:
                return Image.open(io.BytesIO(await stream.read()))

    async def _save_image_to_s3(self, file_key, image):
        async with self.s3_session.create_client(service_name='s3',
                                                 aws_secret_access_key=configs.SECRET_KEY,
                                                 aws_access_key_id=configs.ACCESS_KEY,
                                                 endpoint_url=configs.MINIO_URL) as client:
            await client.delete_object(Bucket=configs.PHOTOS_BASKET, Key=file_key)
            await client.put_object(Bucket=configs.PHOTOS_BASKET, Key=file_key, Body=image)

    async def _run_merge_job(self, file_id, file_key, content_image, style_image):
        buf = io.BytesIO()
        image = await self.loop.run_in_executor(None,
                                                style_transfer_nn.merge_photo,
                                                content_image,
                                                style_image)
        Image.fromarray(image).save(buf, format='PNG')
        await self._save_image_to_s3(file_key, buf.getvalue())
        await self._mark_photo_as_ready(file_id)

    async def on_server_shutdown(self, application):
        if self.scheduler is not None:
            await self.scheduler.close()

    async def startup(self):
        """
        Pre-loader for application, this method create
        database pool and set available routers.
        """
        self.db_pool = await aiopg.create_pool(configs.DSN)
        self.scheduler = await aiojobs.create_scheduler()
        self.s3_session = aiobotocore.get_session()
        self.router.add_routes([web.post('/merge', self.merge)])
        await super().startup()

    async def merge(self, request):
        data = await request.json()

        # Get photo key and filter key
        filter_key = await self._get_key_from_db(configs.STYLES_BASKET, data["filter_id"])
        photo_key = await self._get_key_from_db(configs.PHOTOS_BASKET, data["photo_id"])

        # Load photo and filter image
        filter_image = await self._get_image_from_s3(configs.STYLES_BASKET, filter_key)
        photo = await self._get_image_from_s3(configs.PHOTOS_BASKET, photo_key)

        await self.scheduler.spawn(self._run_merge_job(data["photo_id"], photo_key, photo, filter_image))
        return web.Response(text='Job created!')


if __name__ == '__main__':
    vgg19_model_loader.load_vgg19_if_not_exists()
    server = Server()
    web.run_app(server)
