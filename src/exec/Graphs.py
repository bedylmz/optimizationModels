import numpy as np
import matplotlib.pyplot as plt

def loadFile(filename):
    data = np.loadtxt(filename)
    return data

def isValid(filename):
    try:
        data = np.loadtxt(filename)
        return True
    except FileNotFoundError:
        return False
    except FileExistsError:
        return False

def indicateTF(line):
    if(line.strip() == "true"): 
        return True
    else:
        return False
    
def graphPlotter(xAxis, yAxis, titles, graphsCount, gdIsValid, sgdIsValid, adamIsValid):
    if(graphsCount == 1):
        fig, axs = plt.subplots(1, 1, figsize=(10, 8))
    elif(graphsCount == 2):
        fig, axs = plt.subplots(1, 2, figsize=(10, 8))
    elif(graphsCount == 3):
        fig, axs = plt.subplots(1, 3, figsize=(10, 8))
    else:
        fig, axs = plt.subplots(2, 2, figsize=(10, 8))

    graphSeparator = 0
    if(gdIsValid and sgdIsValid and adamIsValid):
        graphSeparator = 3
    elif(gdIsValid and sgdIsValid and not adamIsValid):
        graphSeparator = 2
    elif(gdIsValid and not sgdIsValid and adamIsValid):
        graphSeparator = 2
    elif(not gdIsValid and sgdIsValid and adamIsValid):
        graphSeparator = 2
    else:
        graphSeparator = 1

    print(graphsCount)
    if(graphsCount == 1):

        addIterator  = 0
        axs.grid()
        axs.set_title(titles[0]+titles[1])

        if(gdIsValid):
            axs.plot(xAxis[addIterator], yAxis[addIterator] ,c='red',marker='.', markersize= 4, 
            label= 'Gradient Descent')
            addIterator += 1
        if(sgdIsValid):
            axs.plot(xAxis[addIterator], yAxis[addIterator] ,c='green',marker='.', markersize= 4,
                label= 'Stochastic Gradient Descent')
            addIterator += 1
        if(adamIsValid):
            axs.plot(xAxis[addIterator], yAxis[addIterator] ,c='blue',marker='.', markersize= 4,
                label= 'ADAM')
        
        axs.legend()
        plt.tight_layout()  # Adjust layout
        plt.show()
    elif(graphsCount <= 3):
        for i in range(0, graphsCount):

            addIterator  = 0
            
            axs[i].grid()
            axs[i].set_title(titles[0]+titles[i+1])

            if(gdIsValid):
                axs[i].plot(xAxis[addIterator], yAxis[i*graphSeparator+addIterator] ,c='red',marker='.', markersize= 4, 
                label= 'Gradient Descent')
                addIterator += 1
            if(sgdIsValid):
                axs[i].plot(xAxis[addIterator], yAxis[i*graphSeparator+addIterator] ,c='green',marker='.', markersize= 4,
                    label= 'Stochastic Gradient Descent')
                addIterator += 1
            if(adamIsValid):
                axs[i].plot(xAxis[addIterator], yAxis[i*graphSeparator+addIterator] ,c='blue',marker='.', markersize= 4,
                    label= 'ADAM')
            
            axs[i].legend()
        plt.tight_layout()  # Adjust layout
        plt.show()
        
    else:
        for i in range(0, graphsCount):

            index1 = 0
            index2 = 0

            if(i == 1):
                index2 = 1
            elif(i == 2):
                index1 = 1
            elif(i == 3):
                index1 = 1
                index2 = 1

            addIterator  = 0

            axs[index1, index2].grid()
            axs[index1, index2].set_title(titles[0]+titles[i+1])

            if(gdIsValid):
                axs[index1, index2].plot(xAxis[addIterator], yAxis[i*graphSeparator+addIterator] ,c='red',marker='.', markersize= 4, 
                label= 'Gradient Descent')
                addIterator += 1
            if(sgdIsValid):
                axs[index1, index2].plot(xAxis[addIterator], yAxis[i*graphSeparator+addIterator] ,c='green',marker='.', markersize= 4,
                    label= 'Stochastic Gradient Descent')
                addIterator += 1
            if(adamIsValid):
                axs[index1, index2].plot(xAxis[addIterator], yAxis[i*graphSeparator+addIterator] ,c='blue',marker='.', markersize= 4,
                    label= 'ADAM')
            axs[index1, index2].legend()
        #plt.tight_layout()  # Adjust layout
        plt.show()

time = False
iteration = False

trainLoss = False
testLoss = False
trainGuess = False
testGuess = False

graphsCount = 0

with open("data\\graphs.dat", "r") as file:
    for index, line in enumerate(file):
        if(index == 1): 
            time = indicateTF(line)
        elif(index == 2): 
            iteration = indicateTF(line)
        elif(index == 3): 
            trainLoss = indicateTF(line)
        elif(index == 4): 
            testLoss = indicateTF(line)
        elif(index == 5): 
            trainGuess = indicateTF(line)
        elif(index == 6): 
            testGuess = indicateTF(line)

xAxis = []
yAxis = []
titles = []
gdIsValid = isValid("data\\gdTime.txt")
sgdIsValid = isValid("data\\sgdTime.txt")
adamIsValid = isValid("data\\adamTime.txt")

#------------------------------------------------------time
if(time):
    if(gdIsValid): 
        timegd = loadFile   ("data\\gdTime.txt")
        xAxis.append(timegd)

    if(sgdIsValid): 
        timesgd = loadFile  ("data\\sgdTime.txt")
        xAxis.append(timesgd)

    if(adamIsValid): 
        timeadam = loadFile ("data\\adamTime.txt")
        xAxis.append(timeadam)

    titles.append("Time - ")

#------------------------------------------------------iteration
if(iteration):
    if(gdIsValid): 
        iterationgd = loadFile   ("data\\gdIteration.txt")
        xAxis.append(iterationgd)
   
    if(sgdIsValid): 
        iterationsgd = loadFile  ("data\\sgdIteration.txt")
        xAxis.append(iterationsgd)
   
    if(adamIsValid): 
        iterationadam = loadFile ("data\\adamIteration.txt")
        xAxis.append(iterationadam)
    
    titles.append("Iteration - ")

#------------------------------------------------------trainLoss
if(trainLoss):
    if(gdIsValid): 
        lossTraingd = loadFile   ("data\\gdLossTrain.txt")
        yAxis.append(lossTraingd)
    if(sgdIsValid): 
        lossTrainsgd = loadFile  ("data\\sgdLossTrain.txt")
        yAxis.append(lossTrainsgd)
    if(adamIsValid): 
        lossTrainadam = loadFile ("data\\adamLossTrain.txt")
        yAxis.append(lossTrainadam)
    graphsCount += 1

    titles.append("Train Loss")

#------------------------------------------------------testLoss
if(testLoss):
    if(gdIsValid): 
        lossTestgd = loadFile   ("data\\gdLossTest.txt")
        yAxis.append(lossTestgd)
    if(sgdIsValid): 
        lossTestsgd = loadFile  ("data\\sgdLossTest.txt")
        yAxis.append(lossTestsgd)
    if(adamIsValid): 
        lossTestadam = loadFile ("data\\adamLossTest.txt")
        yAxis.append(lossTestadam)
    graphsCount += 1
    titles.append("Test Loss")

#------------------------------------------------------trainGuess
if(trainGuess):
    if(gdIsValid): 
        correctTraingd = loadFile   ("data\\gdCorrectTrain.txt")
        yAxis.append(correctTraingd)
    if(sgdIsValid): 
        correctTrainsgd = loadFile  ("data\\sgdCorrectTrain.txt")
        yAxis.append(correctTrainsgd)
    if(adamIsValid): 
        correctTrainadam = loadFile ("data\\adamCorrectTrain.txt")
        yAxis.append(correctTrainadam)
    
    graphsCount += 1
    titles.append("Correct Guess in Train")

#------------------------------------------------------testGuess
if(testGuess):
    if(gdIsValid): 
        correctTestgd = loadFile   ("data\\gdCorrectTest.txt")
        yAxis.append(correctTestgd)
    if(sgdIsValid): 
        correctTestsgd = loadFile  ("data\\sgdCorrectTest.txt")
        yAxis.append(correctTestsgd)
    if(adamIsValid): 
        correctTestadam = loadFile ("data\\adamCorrectTest.txt")
        yAxis.append(correctTestadam)
    graphsCount += 1
    titles.append("Correct Guess in Test")

graphPlotter(xAxis, yAxis, titles, graphsCount, gdIsValid, sgdIsValid, adamIsValid)
