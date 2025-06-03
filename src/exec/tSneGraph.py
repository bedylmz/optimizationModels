import numpy as np
import matplotlib.pyplot as plt
from sklearn.manifold import TSNE

def include(filename):

    index = 0
    #dataSum = np.array

    if(filename == "gd.txt"):
        index = 2
    elif(filename == "sgd.txt"):
        index = 3
    else:
        index = 4
    randomness = 0.0057

    dataSum = np.loadtxt(filename[:index]+str(1)+filename[index:])
    dataSum = dataSum
    
    for i in range(2,6):
        data = np.loadtxt(filename[:index]+str(i)+filename[index:])
        noise = np.random.randn(*data.shape) * randomness
        dataSum = np.concatenate((dataSum, data), axis = 0)

    return dataSum

def load():
    data = include("gd.txt")
    data = np.concatenate((data,include("sgd.txt")), axis=0)
    data = np.concatenate((data,include("adam.txt")), axis=0)
    
    tsne = TSNE(n_components = 2, perplexity = 6800, random_state = 100)
    data_2d = tsne.fit_transform(data)
    return data_2d

def TsnePlotter(data, sample, color, markerType, labelName): 
    data_subset = data
    stepwise_data = data_subset[::2]
    index = 0
    plt.scatter(stepwise_data[:, 0], stepwise_data[:, 1], c=color, alpha= 0.35,marker=markerType, label=labelName)
    for x, y in stepwise_data:
        index = index + 1
        plt.text(x, y, str(index), fontsize = 8, color='#000000', alpha=0.7)
    #for i in range(0,sample):
    #    plt.annotate(str(i), (stepwise_data[i, 0], stepwise_data[i, 1]), fontsize= 4)

plt.figure(figsize=(8, 6))
plt.grid()


data_2d = load()
"""
TsnePlotter(data_2d[0:280], int(280/2-1),'#fc0303','o','gd1')
TsnePlotter(data_2d[280:559], int(279/2-1),'#fcf803','o','gd2')
TsnePlotter(data_2d[559:840], int(281/2-1),'#07fc03','o','gd3')
TsnePlotter(data_2d[840:1120], int(280/2-1),'#031cfc','o','gd4')
TsnePlotter(data_2d[1120:1400], int(280/2-1),'#e803fc','o','gd5')
"""
"""TsnePlotter(data_2d[1400:1706], int(306/2-1),'#fc0303','o','sgd1')
TsnePlotter(data_2d[1706:2213], int(507/2-1),'#fcf803','o','sgd2')
TsnePlotter(data_2d[2213:2663], int(450/2-1),'#07fc03','o','sgd3')
TsnePlotter(data_2d[2663:3110], int(447/2-1),'#031cfc','o','sgd4')
TsnePlotter(data_2d[3110:3453], int(343/2-1),'#e803fc','o','sgd5')"""

TsnePlotter(data_2d[3453:4320], int(867/2-1),'#fc0303','o','adam1')
TsnePlotter(data_2d[4320:4925], int(604/2-1),'#fcf803','o','adam2')
TsnePlotter(data_2d[4925:5640], int(715/2-1),'#07fc03','o','adam3')
TsnePlotter(data_2d[5640:6339], int(699/2-1),'#031cfc','o','adam4')
TsnePlotter(data_2d[6339:7207], int(868/2-1),'#e803fc','o','adam5')



plt.title('2D Representation of W\'s using t-SNE')
plt.xlabel('t-SNE Component 1')
plt.ylabel('t-SNE Component 2')
plt.legend()
plt.show()
