################################
# CPS842 Fall 2021
# Assignment 2
# Name: Tusaif Azmat.
# Student# 500660278.
################################
import datetime
import search
import re
from porter import PorterStemmer


def print_name():
    print('Student Name: Tusaif Azmat \nStudent number: 500660278. \n')


def evaluate_performance(K_value):
    with_stem = False
    stop_words = False
    posting_file = open('postings.txt', 'r')
    check = posting_file.read().replace('\n', ' ')
    if check[0] == '1':
        with_stem = True
    if check[1] == '1':
        stop_words = True
    posting_file.close()
    # opening query file
    query_file = open('query.text', 'r')
    query_content = query_file.readlines()
    query_file.close()
    query_list = evaluate_queries(query_content, with_stem, stop_words)
    # opening qrels file
    grels_file = open('qrels.text', 'r')
    rel_context = grels_file.read().replace('\n', ' ').split()
    grels_file.close()
    rel_context = list(filter('0'.__ne__, rel_context))
    rel_context = [int(x) for x in rel_context]
    relevance_dict = dict.fromkeys(range(1, len(query_list) + 1))
    i = 1
    while i < len(rel_context):
        if relevance_dict[rel_context[i - 1]] is None:
            relevance_dict[rel_context[i - 1]] = []
        relevance_dict[rel_context[i - 1]].append(rel_context[i])
        i += 2

    result_list = []
    i = 1
    for query in query_list:
        print('Calculating results for query #', str(i), ' from query file:')
        result_list.append(search.query_search(query, False, K_value))
        i += 1
    # calculate Mean Average Precision(MAP) and R-precision
    # The final output will be the average MAP and R-Precision values over all queries.
    print('\n**************************')
    print('**************************')
    print('Now Calculating To evaluate MAP/R-P...')
    print('**************************')
    average_precisions = []
    r_precisions = []
    for i in range(len(result_list)):
        print("Query #: " + str(i + 1))
        if relevance_dict[i+1] is not None:
            average_precisions.append(calculate_map_rp(result_list[i], relevance_dict[i + 1])[0])
            r_precisions.append(calculate_map_rp(result_list[i], relevance_dict[i + 1])[1])
            print('(MAP) value: ', str(average_precisions[-1]))
            print('R-Precision: ', str(r_precisions[-1]))
        else:
            print('*** No relevant document Found..., ')
    m_a_p = sum(average_precisions) / len(average_precisions)
    r_pr = sum(r_precisions) / len(r_precisions)
    print('------------------------------------------------')
    print('************************************************')
    print('------------------------------------------------')
    print('Over all Average MAP is: ', str(m_a_p))
    print('Over all Average R-Precision is: ', str(r_pr))


# calculate Mean Average Precision and R-precision
def calculate_map_rp(query_results, relevant_results):
    relevant_found = 0
    r_counter = 0
    total_found = 0
    precision_history = []
    i = 0
    for result in query_results:
        i += 1
        total_found += 1
        if result in relevant_results:
            relevant_found += 1
            precision_history.append(relevant_found / total_found)
            if i < len(query_results):
                r_counter += 1

    r_precision = r_counter / len(query_results)
    return [sum(precision_history) / len(relevant_results), r_precision]


def evaluate_queries(lines, use_stem, use_stop_word):
    stop_words = []
    if use_stop_word:
        stop_words = open('stopwords.txt', 'r').read().split('\n')
    query_text = ''
    queries = []
    start_scan = False
    for line in lines:
        if line.startswith('.'):
            start_scan = False
        if start_scan is True:
            words = str.split(line)
            for word in words:
                word = word.lower()
                word = re.sub('[^A-Za-z0-9$\-]+', '', word)
                if use_stem:
                    word = stem(word)
                if use_stop_word:
                    if word not in stop_words:
                        query_text += word + " "
                else:
                    if word != '':
                        query_text += word + " "
        if line.startswith('.W'):
            start_scan = True
        if line.startswith('.N'):
            queries.append(query_text)
            query_text = ''
    return queries


def stem(word):
    p = PorterStemmer()
    return p.stem(word, 0, len(word) - 1)


if __name__ == '__main__':
    print_name()
    print(' ********************************************** ')
    print('  The performance of the IR system underway... ')
    print(' ********************************************** ')
    print('')
    K = 20
    start = datetime.datetime.now()
    evaluate_performance(K)
    finish = datetime.datetime.now()
    print('Time Taken by system: ', finish - start)
    print('------------------------------------------------')
    print('************************************************')
    print('------------------------------------------------')
