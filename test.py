################################
# CPS842 Fall 2021
# Assignment 1
# Name: Tusaif Azmat.
# Student# 500660278.
################################
import time


def search_query_term(dictionary_obj, posting_obj, doc_word_obj):
    timer_list = []
    print("Enter your term to search below or enter ZZEND to end the program. ")
    word_search = input('Enter a Single Term to Search: ')
    while word_search != 'ZZEND':
        word_search = word_search.lower()
        start_timer = time.time()
        if word_search in dictionary_obj:
            print('Document frequency :', dictionary_obj[word_search])
            post_index = posting_obj[word_search]
            for docID in post_index:
                freq_doc = post_index[docID][0]
                term_pos = str(post_index[docID][1]).lstrip('[').rstrip(']')
                print('Doc ID:', docID, '.\n Title:', " ".join(doc_word_obj[docID][0]), '.\n Frequency in doc ',
                      freq_doc, ' in position(s)', term_pos)
                extraInfo(docID, word_search, doc_word_obj)
        else:
            print(word_search, "is Not found in the Document")
        end_timer = time.time()
        print(end_timer - start_timer)
        timer_list.append(end_timer - start_timer)
        word_search = input('Enter a Single Term to search:')
    print("End of the search.")
    return timer_list


def extraInfo(x, y, wordObj):
    if x in wordObj:
        if y in [item.lower() for item in wordObj[x][0]]:
            pos = [item.lower() for item in wordObj[x][0]].index(y)
            if len(wordObj[x][0]) <= 11:
                print(" ".join(wordObj[x][0]))
            else:
                if pos - 5 < 0:
                    left = abs(pos - 5)
                    summary = wordObj[x][0][0:pos + 1]
                    summary.extend(wordObj[x][0][pos + 1:pos + left + 6])
                    print(" ".join(summary))
                elif pos + 5 > len(wordObj[x][0]):
                    left = (len(wordObj[x][0]) + 1) - pos
                    summary = wordObj[x][0][pos - 5 - left:pos] + wordObj[x][0][pos:len(wordObj[x][0]) + 1]
                    print(" ".join(summary))
                else:
                    print(" ".join(wordObj[x][0][pos - 5:pos + 6]))
        else:
            pos = [item.lower() for item in wordObj[x][1]].index(y)
            if len(wordObj[x][1]) <= 11:
                print(" ".join(wordObj[x][1]))
            else:
                if pos - 5 < 0:
                    left = abs(pos - 5)
                    summary = wordObj[x][1][0:pos + 1]
                    summary.extend(wordObj[x][1][pos + 1:pos + left + 6])
                    print(" ".join(summary))
                elif pos + 5 > len(wordObj[x][1]):
                    left = (len(wordObj[x][1]) + 1) - pos
                    summary = wordObj[x][1][pos - 5 - left:pos] + wordObj[x][1][pos:len(wordObj[x][1]) + 1]
                    print(" ".join(summary))
                else:
                    print(" ".join(wordObj[x][1][pos - 5:pos + 6]))


def search_time_taken(each_time):
    # compute the search time taken
    avg = sum(each_time) / len(each_time)
    print('Average query search time is: ', avg)


if __name__ == "__main__":
    # First open the files for reading that were created by invert.py file
    with open('dictionary.txt', 'r') as file01_dict:
        dict_loaded = dict(eval(file01_dict.read()))
    with open('posting.txt', 'r') as file02_post:
        post_loaded = dict(eval(file02_post.read()))
    with open('doc_word.txt', 'r') as file03_doc_word:
        word_loaded = dict(eval(file03_doc_word.read()))
    query_time_taken = search_query_term(dict_loaded, post_loaded, word_loaded)
    search_time_taken(query_time_taken)
