
# coding: utf-8

# In[1]:


from bs4 import BeautifulSoup
import os
import operator
import re
from nltk import PorterStemmer
from numpy import linalg as LA
import math


# In[2]:


import csv


# In[3]:


stemmer = PorterStemmer()


# In[4]:


base_path = 'reuters21578'


# In[5]:


#Reading the stoplist file
with open(os.path.join(base_path,'stoplist.txt')) as f:
    stoplist = f.read().splitlines()

stoplist = ''.join(stoplist)


# In[6]:


count_dict = {}


# In[7]:


filename_list = []


# In[8]:


for file in os.listdir(base_path):
    filename_list.append(file)


# In[9]:


#filename_list


# In[10]:


#Finding count of topics from the body tag
for filename in filename_list:
    if filename.endswith('.sgm'):
        file_path = os.path.join(base_path,filename)
        soup = BeautifulSoup(open(file_path,'r').read(), "html.parser")
        contents = soup.findAll('reuters')
        #print(len(contents))
        for c in contents:
            x = c.findChildren('topics')
            #print(x[0].text)
            a = c.findChildren('body')
            z = x[0].findChildren()
            #print(count_dict)
            #body = b[0].text
            #print(z.text)
            if len(z) == 1:
                topic = z[0].string
                if topic in count_dict:
                    count_dict[topic] += 1
                else:
                    count_dict[topic] = 1


# In[11]:


#print(count_dict)


# In[12]:


sorted_count_dict = sorted(count_dict.items(), key = operator.itemgetter(1), reverse =True)
#print(sorted_count_dict)


# In[13]:


top_20 = sorted_count_dict[0:20]
#top_20


# In[14]:


def clean_up(a_string):
    #Remove non-ascii characters:
    a_string = ''.join([x for x in a_string if ord(x) < 128])
    
    #Change to lowercase
    a_string = a_string.lower()
    
    #Replace non alphanumeric with space
    a_string = re.sub('[^0-9a-zA-Z]+', ' ', a_string)
    
    #Split into tokens
    a_token = a_string.split(' ')
    
    print(len(a_token))
    
    #Eliminate tokens that contain only digits or if they are in stoplist
    a_token = [x for x in a_token if not (x.isdigit())] 
    # or x in stoplist)]
    
    #Eliminate tokens in stoplist
    a_token = [a for a in a_token if not (a in stoplist)]
    
    #Obtain stem of each token
    token_stemmed = [stemmer.stem(item) for item in a_token]
    
    #Remove tokens that occur less than 5 times
    return(token_stemmed)


# In[15]:


token_count = {}
out = open("output.txt","w")
line_count = 0
for filename in filename_list:
    if filename.endswith('.sgm'):
        file_path = os.path.join(base_path,filename)
        soup = BeautifulSoup(open(file_path,'r').read(), "html.parser")
        contents = soup.findAll('reuters')
        for content in contents:
            topics = content.findChildren('topics')
            body = content.findChildren('body')
            z = topics[0].findChildren()
            if len(z) == 1 and len(body) > 0:
                topic = z[0].string
                body_string = body[0].string
                for item in top_20:
                    if topic in item[0]:
                        out.write(topic + " " + str(content['newid']) + " " + " ".join(clean_up(body_string)) + "\n")
                        for k in clean_up(body_string):
                            if k in token_count:
                                token_count[k] += 1
                            else:
                                token_count[k] = 1
                        line_count += 1


# In[16]:


out.close()


# In[17]:


#line_count


# In[18]:


with open("output.txt","r") as infile:
    text = infile.read().splitlines()


# In[19]:


count_freq = {}
for line in text:
    sp = line.split(' ')
    #print((sp[218]))
    for word in sp[2:]:
        #print(word)
        if token_count[word] >= 5:
            if word in count_freq:
                count_freq[word] += 1
            else:
                count_freq[word] = 1
#print(len(count_))


# In[20]:


#print(len(count_freq))


# In[21]:


#print('humid' in count_freq)


# In[33]:


# Writing label file
file = open('reuters21578.clabel','w')
coun = 0
for word in count_freq:
    coun += 1
    file.write(word + ',' + str(coun) + '\n')


# In[32]:


#Creating vector dictionaries

#1 Frequency
freq_dict = {}

for line in text:
    sp = line.split(' ')
    freq_dict[sp[1]] = []
    article = sp[2:]
    for word in count_freq:
        val = article.count(word)
        freq_dict[sp[1]].append(val)
        
#2 Square root
sqrt_dict = {}

for line in text:
    sp = line.split(' ')
    sqrt_dict[sp[1]] = []
    article = sp[2:]
    for word in count_freq:
        val = article.count(word)
        if val != 0:
            val1 = 1 + math.sqrt(val)
        else:
            val1 = val        
        sqrt_dict[sp[1]].append(val1)
        
#3 Log
log_dict = {}

for line in text:
    sp = line.split(' ')
    log_dict[sp[1]] = []
    article = sp[2:]
    for word in count_freq:
        val = article.count(word)
        if val != 0 :
            val1 = 1 + (math.log(val)/math.log(2))
        else:
            val1 = val
        log_dict[sp[1]].append(val1)
            


# In[31]:


# Writing class file
file = open('reuters21578.class','w')
for line in text:
    sp = line.split(' ')
    topic = sp[0]
    article_id = sp[1]
    file.write(article_id + ',' + topic + '\n')


# In[23]:


# Normalising the frequency vector

file = open('freq.csv','w')

for k,v in freq_dict.items():
    #v = [i/LA.norm(v) for i in v]
    v /= LA.norm(v)
    for index, item in enumerate(v):
        if item != 0:
            file.write(k + ',' + str(index) +',' + str(item) + '\n')
        
file.close()


# In[25]:


# Normalising the sqrt vector

file = open('sqrt.csv','w')

for k,v in sqrt_dict.items():
    v /= LA.norm(v)
    for index, item in enumerate(v):
        if item != 0:
            file.write(k + ',' + str(index) +',' + str(item) + '\n')
        
file.close()


# In[24]:


# Normalising the log vector

file = open('log.csv','w')

for k,v in log_dict.items():
    v /= LA.norm(v)
    for index, item in enumerate(v):
        if item != 0:
            file.write(k + ',' + str(index) +',' + str(item) + '\n')
        
file.close()

