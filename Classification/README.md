Implementing various classifiers for hand-written digit recognition.  
This project involve using different representations of the dataset, implementing three different classifier models  
and assessing the performance of each classifier with different representations. It also includes  
analyzing some of the models that were estimated.

Data - MNIST data, divided into train, validation and test.

Classification algorithms: 
- K nearest-neighbor 
- Ridge regression

The program inputs CSV-representations of train, validation, and test datasets.  
For Knn, the program should uses the validation set to measure the performance of different values of K in the range of 1-20, 
and selects the value of K that achieves the best performance. Then it combines the train and validation set into a larger training set   
and uses the selected value of K to classify the test set and measure its performance.

For Ridge Regression, since there are 10 classes, the program builds 10 one-vs-rest binary classifiers, in which the +ve class is   
encoded as +1 and the -ve class is encoded as -1. Using these 10 binary models, an instance is assigned to the class whose   
corresponding one-vs-rest binary model results in the highest prediction value (argmax approach).   
The program chooses  values of lambda and uses the validation set to evaluate the performance of these models.
The train and validation sets are then combined and estimated lambda and 2 * estimated lambda are used to classify the test set.

Accuracy is used to measure performance of the classifier.
