# -*- coding: utf-8 -*-
import matplotlib.pyplot as plt
import numpy as np


def visualiseFile(filename):
    def pltLineGraph(X, Y, yLabel, label):
        plt.rc('figure')
        plt.plot(X, Y, label=label)
        plt.xlabel('epoch', fontsize=10)
        plt.ylabel(yLabel, fontsize=10)
        plt.title('{}-Epoch Graph'.format(yLabel), fontsize=15)
        plt.xticks(np.arange(0, len(X), len(X)//10))
        plt.legend()
        plt.show()

    with open(filename, 'r') as f: lines = f.readlines()  # read lines
    epochs, objectives, objectivesInEpoch = [], [], []
    for i, line in enumerate(lines):
        if line.split()[0] == 'Epoch:':
            epochs.append(line.split()[1])
            if i != 0: objectives.append(objectivesInEpoch)
            objectivesInEpoch = []
        else:
            singleObjs = [float(obj.lstrip('[').rstrip(',').rstrip(']')) for obj in line.split()]
            objectivesInEpoch.append(singleObjs)
            if i == len(lines)-1: objectives.append(objectivesInEpoch)

    # check len matched
    if len(epochs) != len(objectives): print("Error: the number of epoch and objectives from source file not matched")

    times = [[j[0] for j in objectives[i]] for i in range(len(objectives))]
    profits = [[j[1] for j in objectives[i]] for i in range(len(objectives))]

    minTimes = [min(i) for i in times]
    maxProfits = [-min(i) for i in profits]  # profit是负数
    for i in range(len(maxProfits)):
        if maxProfits[i] == -1.7976931348623157e+308: maxProfits[i] = 1.7976931348623157e+308

    aveTimes = [sum(i)/len(i) for i in times]
    aveProfits = [sum(i)/len(i) for i in times]

    pltLineGraph(epochs, minTimes, yLabel="minTimes", label="times")
    pltLineGraph(epochs, maxProfits, yLabel="maxProfits", label="profits")
    # pltLineGraph(epochs, aveTimes, yLabel="aveTimes", label="times")
    # pltLineGraph(epochs, aveProfits, yLabel="aveProfits", label="profits")


if __name__ == "__main__":
    fileName = './a280-n1395.txt'  # replace to which file you want to visualise
    visualiseFile(fileName)