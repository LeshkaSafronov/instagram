import os
import hashlib
import logging

import configs

from urllib.request import urlopen


__all__ = [
    'check_vgg19_md5_sum',
    'download_vgg19_model',
    'load_vgg19_if_not_exists',
]


def check_vgg19_md5_sum():
    """Check VGG19 model MD5 sum after loading."""
    filehash = hashlib.md5()

    try:
        with open(configs.VGG19_MODEL_NAME, 'rb') as vgg19_file:
            filehash.update(vgg19_file.read())
    except (OSError, IOError) as e:
        logging.error('Calculate MD5 sum failed with error', e)

    # Check md5 sum
    assert configs.VGG19_MODEL_MD5 == filehash.hexdigest()


def download_vgg19_model():
    """Downdload VGG19 model from server."""
    logging.warning('Start loading VGG19 model!')

    try:
        filedata = urlopen(configs.VGG19_MODEL_URL)
        with open(configs.VGG19_MODEL_NAME, 'wb') as vgg19_file:
            vgg19_file.write(filedata.read())
    except (OSError, IOError) as e:
        logging.error('Loading VGG19 model failed with error', e)

    check_vgg19_md5_sum()
    logging.warning('VGG19 model loaded!')


def load_vgg19_if_not_exists():
    """
    Check VGG19 model for exists, if model not found
    downdload it from server and save on current directory.
    """
    if not os.path.exists(configs.VGG19_MODEL_NAME):
        download_vgg19_model()


if __name__ == '__main__':
    load_vgg19_if_not_exists()
