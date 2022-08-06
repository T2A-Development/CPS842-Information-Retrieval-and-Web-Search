################################
# CPS842 Fall 2021
# Assignment 2
# Name: Tusaif Azmat.
# Student# 500660278.
################################

import re
from porter import PorterStemmer


def print_name():
    print('Student Name: Tusaif Azmat \nStudent number: 500660278. \n')


def create_inverted_index():
    use_stop_word = False
    stop_words = []
    use_stem = False
    stop_word_input = input('Would you like to remove stop words? (Yes/No): ')
    stem_input = input('Would you like to use stemming? [Yes/No] ')
    if stop_word_input == 'Y' or stop_word_input == 'Yes' or stop_word_input == 'yes' or stop_word_input == 'y':
        use_stop_word = True
        stop_words = open('stopwords.txt', 'r').read().split('\n')
    if stem_input == 'Y' or stem_input == 'Yes' or stem_input == 'yes' or stem_input == 'y':
        use_stem = True
    term_dict = dict()
    doc_num = 0
    document_pos = 0
    start_scan = False
    read_file = open('cacm.all', 'r')
    if read_file.mode == 'r':
        lines = read_file.readlines()
        for line in lines:
            if line.startswith("."):
                start_scan = False
            if line.startswith(".I"):
                doc_num += 1
                document_pos = 0
            if start_scan is True:
                line = re.sub('[\-]+', ' ', line)
                line = re.sub('(?<=[,])(?=[^\s])(?=[A-Z])', ' ', line)
                words = str.split(line)
                for word in words:
                    document_pos += 1
                    # words are made lower case right from the start
                    word = word.lower()
                    word = re.sub('[^A-Za-z0-9$]+', '', word)
                    if use_stem:
                        word = stemming_option(word)
                    if use_stop_word:
                        if not word in stop_words:
                            fill_dict(word, term_dict, doc_num, document_pos)
                    else:
                        if word != '':
                            fill_dict(word, term_dict, doc_num, document_pos)
            if line.startswith(".T") or line.startswith(".W") or line.startswith(".A"):
                start_scan = True
    print_dict(term_dict, use_stem, use_stop_word)


def fill_dict(word, term_dict, document_num, document_pos):
    if word in term_dict:
        # check to see if there is an entry for the current document
        # for that I need to check the first element of the last list in the list of lists
        # in the value of the dict
        if term_dict[word][-1][0] == document_num:
            term_dict[word][-1][1] += 1
            term_dict[word][-1][2] += [document_pos]
        else:
            term_dict[word].append([document_num, 1, [document_pos]])
    else:
        term_dict[word] = [[document_num, 1, [document_pos]]]


def print_dict(term_dict, use_stem, use_stop):
    post_file = open("postings.txt", "w")
    if use_stem:
        post_file.write("1")
    else:
        post_file.write("0")
    if use_stop:
        post_file.write("1\n")
    else:
        post_file.write("0\n")
    for key, value in sorted(term_dict.items()):
        post_file.write('["' + key + '", ' + str(value) + "],\n")
    post_file.close()


def stemming_option(word):
    ps = PorterStemmer()
    return ps.stem(word, 0, len(word) - 1)


if __name__ == "__main__":
    print_name()
    print(' ************************************ ')
    print('  The index construction underway... ')
    print(' ************************************ ')
    print('')
    print('But first provide the answer to the following options...')
    create_inverted_index()
    print('**** Posting list / dictionary file created ... ****')
