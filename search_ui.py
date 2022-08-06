################################
# CPS842 Fall 2021
# Assignment 2
# Name: Tusaif Azmat.
# Student# 500660278.
################################


import search as run_search


def print_name():
    print('Student Name: Tusaif Azmat \nStudent number: 500660278. \n')


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
            find_query = True
            run_search.query_search(query, find_query, 20)
    print('Good Bye!')
