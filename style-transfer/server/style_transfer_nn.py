import logging
import configs

import scipy.io
import numpy as np
import tensorflow as tf

from PIL import Image


__all__ = [
    'get_vgg19_weight',
    'create_conv2d',
    'create_avg_pool',
    'build_vgg19_model',
    'VGG19_MEAN_VALUES',
]


# VGG19 layers configurations
VGG19_MEAN_VALUES = np.array([123.68, 116.779, 103.939]).reshape((1, 1, 1, 3))


def get_vgg19_weight(all_layers, layer, expected_layer_name):
    """
    Load the weights and bias from the VGG model for a given layer.

    :param all_layers (list): VGG19 model layers, matlab format
    :param layer (str): layer name
    :parma expected_layer_name (str): layer name for checking
    """
    weight = all_layers[0][layer][0][0][0][0][0]
    bias = all_layers[0][layer][0][0][0][0][1]

    # Check layer name
    layer_name = all_layers[0][layer][0][0][-2]
    assert layer_name == expected_layer_name
    return weight, bias


def create_conv2d(all_layers, prev_layer, layer, layer_name):
    """
    Return the Conv2D and ReLu layer using the weights, biases.
    About conv2d layer: https://www.tensorflow.org/api_docs/python/tf/nn/conv2d

    :param all_layers (list): VGG19 model layers, matlab format
    :param prev_layer (Tensor)
    :param layer (str): layer name
    :param layer_name (str): layer name for checking
    """
    weight, bias = get_vgg19_weight(all_layers, layer, layer_name)

    # Create TensorFlow constan variables
    weight = tf.constant(weight)
    bias = tf.constant(np.reshape(bias, bias.size))

    # Passing variables
    layer = tf.nn.conv2d(prev_layer, weight, [1, 1, 1, 1], 'SAME')
    layer = tf.add(layer, bias)
    layer = tf.nn.relu(layer)
    return layer


def create_avg_pool(prev_layer):
    """
    Return the average pooling layer.
    More about avg pool layer: https://www.tensorflow.org/api_docs/python/tf/nn/avg_pool

    :param prev_layer (Tensor)
    """
    return tf.nn.avg_pool(prev_layer, [1, 2, 2, 1], [1, 2, 2, 1], 'SAME')


def build_vgg19_model(vgg19_model_path, input_image_size):
    """
    Build VGG19 model.
    More about VGG19 model: https://www.kaggle.com/keras/vgg19

    :param vgg19_model_path (str): path to pre-trained vgg19 model
    :param input_image_size (tuple): input image dimension
    """
    vgg19 = scipy.io.loadmat(vgg19_model_path)
    vgg19_layers = vgg19["layers"]

    # Constructs the graph model
    with tf.variable_scope('VGG19'):
      graph = {}
      # 1st block

      # FIXME: remove
      graph["input"]      = tf.Variable(np.zeros(input_image_size), dtype = 'float32')
      graph["conv1_1"]    = create_conv2d(vgg19_layers, graph["input"], 0, 'conv1_1')
      graph["conv1_2"]    = create_conv2d(vgg19_layers, graph["conv1_1"], 2, 'conv1_2')

      # 2nd block
      graph["avg_pool_1"] = create_avg_pool(graph["conv1_2"])
      graph["conv2_1"]    = create_conv2d(vgg19_layers, graph["avg_pool_1"], 5, 'conv2_1')
      graph["conv2_2"]    = create_conv2d(vgg19_layers, graph["conv2_1"], 7, 'conv2_2')

      # 3th block
      graph["avg_pool_2"] = create_avg_pool(graph["conv2_2"])
      graph["conv3_1"]    = create_conv2d(vgg19_layers, graph["avg_pool_2"], 10, 'conv3_1')
      graph["conv3_2"]    = create_conv2d(vgg19_layers, graph["conv3_1"], 12, 'conv3_2')
      graph["conv3_3"]    = create_conv2d(vgg19_layers, graph["conv3_2"], 14, 'conv3_3')
      graph["conv3_4"]    = create_conv2d(vgg19_layers, graph["conv3_3"], 16, 'conv3_4')

      # 4th block
      graph["avg_pool_3"] = create_avg_pool(graph["conv3_4"])
      graph["conv4_1"]    = create_conv2d(vgg19_layers, graph["avg_pool_3"], 19, 'conv4_1')
      graph["conv4_2"]    = create_conv2d(vgg19_layers, graph["conv4_1"], 21, 'conv4_2')
      graph["conv4_3"]    = create_conv2d(vgg19_layers, graph["conv4_2"], 23, 'conv4_3')
      graph["conv4_4"]    = create_conv2d(vgg19_layers, graph["conv4_3"], 25, 'conv4_4')

      # 5th block
      graph["avg_pool_4"] = create_avg_pool(graph["conv4_4"])
      graph["conv5_1"]    = create_conv2d(vgg19_layers, graph["avg_pool_4"], 28, 'conv5_1')
      graph["conv5_2"]    = create_conv2d(vgg19_layers, graph["conv5_1"], 30, 'conv5_2')
      graph["conv5_3"]    = create_conv2d(vgg19_layers, graph["conv5_2"], 32, 'conv5_3')
      graph["conv5_4"]    = create_conv2d(vgg19_layers, graph["conv5_3"], 34, 'conv5_4')

      # 6th block
      graph["avg_pool_5"] = create_avg_pool(graph["conv5_4"])
      return graph


def preprocessing(content_image, style_image):
    # Convert images to RGB format
    content_image = content_image.convert('RGB')
    style_image = style_image.convert('RGB')

    # Get images size
    content_image_size = content_image.size
    style_image_size = style_image.size

    # Resize images
    if content_image_size > style_image_size:
        new_w, new_h = style_image_size[0], style_image_size[1]
        content_image = content_image.resize((new_w, new_h), Image.ANTIALIAS)
    elif content_image_size < style_image_size:
        new_w, new_h = content_image_size[0], content_image_size[1]
        style_image = style_image.resize((new_w, new_h), Image.ANTIALIAS)

    # Convert images to numpy array
    content_image = np.asarray(content_image)
    style_image = np.asarray(style_image)

    # Prepare images
    content_image = np.reshape(content_image, (1,) + content_image.shape)
    style_image = np.reshape(style_image, (1,) + style_image.shape)
    return content_image, style_image


def postprocessing(image):
    image = (image + VGG19_MEAN_VALUES)[0]
    image = np.clip(image, 0, 255).astype('uint8')
    return image


def generate_noise_image(content_image):
    """Returns a noise image intermixed with the content image."""
    noise_image = np.random.uniform(-20, 20, content_image.shape).astype('float32')
    input_image = noise_image * configs.NOISE_RATIO + content_image * (1 - configs.NOISE_RATIO)
    return input_image


def set_content_loss_function(sess, model):
    """Content loss function as defined in the paper."""
    p = sess.run(model['conv4_2'])
    x = model['conv4_2']

    # N is the number of filters at layer l
    # M is the height times the width of the feature map at layer l
    N = p.shape[3]
    M = p.shape[1] * p.shape[2]
    return (1 / (4*N*M)) * tf.reduce_sum(tf.pow(x-p, 2))


def get_gram_matrix(F, n, m):
    """The gram matrix G."""
    F_t = tf.reshape(F, (m, n))
    return tf.matmul(tf.transpose(F_t), F_t)


def style_loss(a, x):
    # n is the number of filters at layer l
    # m is the height times the width of the feature map at layer l
    # A is the style representation of the original image at layer l
    # G is the style representation of the generated image at layer l
    n = a.shape[3]
    m = a.shape[1] * a.shape[2]
    A = get_gram_matrix(a, n, m)
    G = get_gram_matrix(x, n, m)
    return (1 / (4*pow(n, 2)*pow(m, 2))) * tf.reduce_sum(tf.pow(G-A, 2))


def set_style_loss_function(sess, model):
    """Style loss function as defined in the paper."""
    E = [style_loss(sess.run(model[layer]), model[layer]) for layer in configs.STYLE_LAYERS]
    return sum([configs.STYLE_VARS[index] * E[index] for index in range(len(configs.STYLE_LAYERS))])


def merge_photo(content_image, style_image):
    content_image, style_image = preprocessing(content_image, style_image)
    vgg19 = build_vgg19_model(configs.VGG19_MODEL_NAME, content_image.shape)

    # Run TensorFlow session
    with tf.Session() as session:
        session.run(tf.global_variables_initializer())
        input_image = generate_noise_image(content_image)

        # Construct content_loss using content_image.
        session.run(vgg19["input"].assign(content_image))
        content_loss = set_content_loss_function(session, vgg19)

        # Construct style_loss using style_image.
        session.run(vgg19["input"].assign(style_image))
        style_loss = set_style_loss_function(session, vgg19)

        # Instantiate equation 7 of the paper.
        total_loss = configs.BETA * content_loss + configs.ALPHA * style_loss

        # Create optimizer
        with tf.variable_scope('optimizer'):
            optimizer = tf.train.AdamOptimizer(2.0)
            train_step = optimizer.minimize(total_loss)

        session.run(tf.global_variables_initializer())
        session.run(vgg19["input"].assign(input_image))

        # Run style transfer
        session.run(tf.global_variables_initializer())
        session.run(vgg19["input"].assign(input_image))

        for epoch in range(5):
            session.run(train_step)
            if epoch % 100 == 0:
                mixed_image = session.run(vgg19["input"])
                logging.warning('Iteration: {}'.format(epoch))
                # logging.warning('Sum: {}'.format(session.run(tf.reduce_sum(mixed_image))))
                # logging.warning('Cost: {}'.format(session.run(total_loss)))
        logging.warning('Finished!')
        return postprocessing(mixed_image)
