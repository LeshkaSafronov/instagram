__all__ = [
    'DSN',
    'ACCESS_KEY',
    'SECRET_KEY',
    'MINIO_URL',
    'STYLES_BASKET',
    'PHOTOS_BASKET',
    'VGG19_MODEL_MD5',
    'VGG19_MODEL_URL',
    'VGG19_MODEL_NAME',
    'NOISE_RATIO',
    'BETA',
    'ALPHA',
    'EPOCHS',
    'STYLE_VARS',
    'STYLE_LAYERS',
]


# VGG19 model params
VGG19_MODEL_NAME = 'imagenet-vgg-verydeep-19.mat'
VGG19_MODEL_URL = 'http://www.vlfeat.org/matconvnet/models/beta16/imagenet-vgg-verydeep-19.mat'
VGG19_MODEL_MD5 = '8ee3263992981a1d26e73b3ca028a123'


# Minio S3 access and secret keys
ACCESS_KEY = 'GRL6DI8QSCK9BJ9IBRRW'
SECRET_KEY = 'g0X9EOzyozntFZZEm0sCK5sGx5+NKRMD9XoyeRFh'
MINIO_URL = 'http://127.0.0.1:9000'


# Minio basket names
STYLES_BASKET = 'filters'
PHOTOS_BASKET = 'photos'


# Database configuration
DSN = 'dbname=neurogram user=postgres password=secret host=127.0.01 port=5432'


# Algorithm configurations
NOISE_RATIO = 0.6
BETA = 5
ALPHA = 100
EPOCHS = 1000


# Style layers configurations
STYLE_LAYERS = ['conv1_1', 'conv2_1', 'conv3_1', 'conv4_1', 'conv5_1']
STYLE_VARS = [0.5, 1.0, 1.5, 3.0, 4.0]
