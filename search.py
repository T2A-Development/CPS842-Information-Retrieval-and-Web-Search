################################
# CPS842 Fall 2021
# Assignment 2
# Name: Tusaif Azmat.
# Student# 500660278.
################################

import json
import numpy as np
import re
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from porter import PorterStemmer


def print_name():
    print('Student Name: Tusaif Azmat \nStudent number: 500660278. \n')


def query_search(user_input, with_query, K):
    use_stem = False
    stop_words = False
    dict_file = open("postings.txt", "r")
    c_file = open("cacm.all", "r")
    content = dict_file.read().replace('\n', ' ')
    if content[0] == "1":
        use_stem = True
    if content[1] == "1":
        stop_words = True
    post_list = json.loads("[" + content[2:-2] + "]")
    c_file_lines = c_file.readlines()
    c_file.close()
    extracted_postings = []
    docs_list = []
    final_list = []
    if dict_file.mode == 'r':
        # get query
        og_query = user_input.lower()
        og_query = re.sub('[\-]+', ' ', og_query)
        og_query = re.sub('[^A-Za-z0-9$ ]+', '', og_query)
        new_query = og_query.split()

        if stop_words:
            temp = []
            stop_words = open("stopwords.txt", "r").read().split('\n')
            for i in range(len(stop_words)):
                stop_words[i] = stop_words[i].lower()
            for word in new_query:
                if word not in stop_words:
                    temp.append(word)
            new_query = temp
        if use_stem:
            stemmed_query = ""
            for word in new_query:
                ps = PorterStemmer()
                word = ps.stem(word, 0, len(word) - 1)
                stemmed_query += word + " "
            new_query = stemmed_query.split()

        new_query.sort()
        term_list = get_term_lists(new_query, post_list)
        # remove duplicates if they exist
        term_list = list(dict.fromkeys(term_list))

        for entry in term_list:
            extracted_postings.append(post_list[entry])
        # get docs out of extracted postings
        for posting in extracted_postings:
            for entry in posting[1]:
                docs_list.append(entry[0])
        docs = list(dict.fromkeys(docs_list))
        docs.sort()
        document_vectors = get_doc_vector(docs, c_file_lines, use_stem, stop_words)
        if document_vectors is None:
            return
        else:
            cosine_list = create_doc_term_matrix(document_vectors, og_query, docs)
            temp_list = []
            for i in range(len(docs)):
                temp_list.append([docs[i], cosine_list[i]])
            temp_list.sort(key=lambda x: x[1])
            temp_list.reverse()
            if with_query:
                print('Your Searched: ', user_input)
                display_results(temp_list, get_doc_info(docs, c_file_lines))
            for elem in temp_list:
                final_list.append(elem[0])
            if K is None:
                return final_list
            else:
                return final_list[:K]


def get_term_lists(query_term, post_list):
    term_list = []
    j = 0
    i = 0
    last_result = 0
    while i < len(post_list) + 1:
        if i == len(post_list):
            if j < len(query_term):
                j += 1
            if j == len(query_term):
                return term_list

            i = last_result
        while query_term[j] == post_list[i][0]:
            term_list.append(i)
            j += 1
            last_result = i
            if j == len(query_term):
                return term_list
        i += 1
    return None


def get_doc_vector(docs, lines, use_stem, use_stop_word):
    if docs is None:
        return
    else:
        stop_words = []
        if use_stop_word:
            stop_words = open("stopwords.txt", "r").read().split('\n')
        i = 0
        document_text = ""
        documents = []
        start_scan = False
        scan_doc = False
        for line in lines:
            if line.startswith("."):
                start_scan = False
            if line.startswith(".I " + str(docs[i])):
                scan_doc = True
            if start_scan is True and scan_doc is True:
                line = re.sub('[\-]+', ' ', line)
                line = re.sub('(?<=[,])(?=[^\s])(?=[A-Z])', ' ', line)
                words = str.split(line)
                for word in words:
                    # words are made lower case right from the start
                    word = word.lower()
                    word = re.sub('[^A-Za-z0-9$]+', '', word)
                    if use_stem:
                        word = stem(word)
                    if use_stop_word:
                        if not word in stop_words:
                            document_text += word + " "
                    else:
                        if word != '':
                            document_text += word + " "
            if (line.startswith(".T") or line.startswith(".W") or line.startswith(".A")) and scan_doc:
                start_scan = True
            if line.startswith(".X") and scan_doc == True:
                scan_doc = False
                documents.append(document_text)
                document_text = ""
                if i < len(docs) - 1:
                    i += 1
                else:
                    break
        return documents


def create_doc_term_matrix(documents, query_term, names):
    vectorizer = TfidfVectorizer(analyzer="word",
                                 tokenizer=None,
                                 preprocessor=None,
                                 stop_words=None,
                                 max_features=5000,
                                 norm='l2',
                                 token_pattern=r"(?u)\b\w+\b")

    df1 = pd.DataFrame({"Query": [query_term]})
    df2 = pd.DataFrame([documents])
    df2.columns = names
    df1 = df1.join(df2)

    # Initialize
    doc_vec = vectorizer.fit_transform(df1.iloc[0])
    # Create dataFrame
    df2 = pd.DataFrame(doc_vec.toarray().transpose(), index=vectorizer.get_feature_names_out())
    # Change column headers
    df1.columns = df2.columns
    arr = df2.to_numpy()
    query_vector = arr[:, 0]
    doc_matrix = np.delete(arr, 0, 1)
    cosine_matrix = (doc_matrix.T * query_vector).T
    cosine_similarity = cosine_matrix.sum(axis=0)
    # print(cosine_similarity)
    return cosine_similarity


def display_results(result_list, info_list):
    # display rank order, document title and document author
    # display 20 and ask to display all
    rank = 1
    for i in range(len(result_list)):
        if i == 20:
            continue_printing = input('\n Do you want to see the rest of the results?(Y/N)')
            if continue_printing == 'N' or continue_printing == 'n' or continue_printing == 'No':
                return
        print('***************************')
        print('  Doc Rank:', str(rank), '\n  Document#: ', str(result_list[i][0]))
        doc_entry = modified_binary_search(info_list, 0, len(info_list), result_list[i])
        print('  Title: ' + info_list[doc_entry][1])
        print('  Author: ' + info_list[doc_entry][2])
        rank += 1


def modified_binary_search(arr, left, right, x):
    # Check base case
    if right >= left:
        mid = left + (right - left) // 2
        # If element is present at the middle itself
        if arr[mid][0] == x[0]:
            return mid
            # If element is smaller than mid, then it
        # can only be present in left sub_array
        elif arr[mid][0] > x[0]:
            return modified_binary_search(arr, left, mid - 1, x)
            # Else the element can only be present
        # in right sub_array
        else:
            return modified_binary_search(arr, mid + 1, right, x)
    else:
        # Element is not present in the array
        return -1


def get_doc_info(docs, lines):
    i = 0
    doc_title = ""
    doc_author = ""
    info_list = []
    start_scan = False
    scan_doc = False
    scan_bit = True
    for line in lines:
        if line.startswith("."):
            start_scan = False
        if line.startswith(".I " + str(docs[i])):
            scan_doc = True
        if start_scan is True and scan_doc is True:
            line = line.replace('\n', " ")
            if scan_bit:
                doc_title += line
            else:
                doc_author += line
        if line.startswith(".T") and scan_doc:
            start_scan = True
            scan_bit = True
        if line.startswith(".A") and scan_doc:
            start_scan = True
            scan_bit = False
        if line.startswith(".X") and scan_doc:
            scan_doc = False
            info_list.append([docs[i], doc_title, doc_author])
            doc_author = doc_title = ""
            if i < len(docs) - 1:
                i += 1
            else:
                break
    return info_list


def stem(word):
    p = PorterStemmer()
    return p.stem(word, 0, len(word) - 1)


if __name__ == "__main__":
    print_name()
    print(' **************************************************************************** ')
    print('  Search for the retrieval process using the vector space model underway... ')
    print(' **************************************************************************** ')
    print('')
    query = ''
    while query != 'zzend':
        query = input("Enter query to search: ").lower()
        if query == '':
            print('You must enter a query to search...Please try again. ')
        elif query == 'zzend':
            print('Thank you for using the retrieval system...')
        else:
            user_query = True
            query_search(query, user_query, 20)
    print('Good Bye!')
