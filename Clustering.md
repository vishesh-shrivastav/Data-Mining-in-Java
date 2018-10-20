Clustering algorithms based on k-means for clustering objects corresponding to sparse high dimensional vectors. 
The project consists of multiple components that involve getting the dataset, selecting the subset to cluster, 
pre-processing the dataset to convert it into a sparse representation, clustering the dataset, 
and evaluating the quality of the clustering solution.
  
All pre-processing done in Python. Clustering algorithm written in java.

**Getting and Subsetting the data:**
The dataset is derived from the "Reuters-21578 Text Categorization Collection Data Set"  that is available at 
the UCI Machine Learning Repository. Only the articles that contain a single topic have been used.
From these articles, only the articles that  have their topics in the 20 most frequent topics are retained. 
From this set of articles, the NEWID number (in the <REUTERS> tag attribute), 
the topic, and the text that is included within the <BODY>...</BODY> tags are exctracted.  
The body of the article formss the text is used for clustering, 
the topic acts as a class label for evaluation, and NEWID is used as the ID of the article.

**Preprocessing steps - convert obtainded data to sparse representation**

- Eliminate any non-ascii characters.
- Change the character case to lower-case.
- Replace any non alphanumeric characters with space.
- Split the text into tokens, using space as the delimiter.
- Eliminate any tokens that contain only digits.
- Eliminate any tokens from the stop list that is provided (file stoplist.txt).
- Obtain the stem of each token using Porter's stemming algorithm;
- Eliminate any tokens that occur less than 5 times.

**Vector representation**

For each document, three different representations derived,
by using the following approaches to assign a value to each of the document's vector dimension 
that corresponds to a token t that it contains:

- The value is the actual number of times that t occurs in the document (frequency).
- The value is 1+sqrt(frequency) for those terms that have a non-zero frequency.
- The value is 1+log2(frequency) for those terms that have a non-zero frequency.

**Clustering:**

Clustering algorithms written for these two criterion functions:
- Standard sum-of-squared-errors criterion function of k-means
- I2 criterion function (spherical k-means)

Quality of these clustering solutions are evaluated by computing entropy and purity.
