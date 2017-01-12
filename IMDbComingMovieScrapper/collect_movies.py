import requests
from bs4 import BeautifulSoup
import datetime


def get_relevant_months(months_before=2, months_after=2):
    d = datetime.date.today()
    a = d.month
    relevant_months = []
    # Before
    year = d.year
    for month in range(d.month - 1, d.month - (months_before + 1), -1):
        adjusted_month = (month % 12)
        if adjusted_month == 0:
            adjusted_month = 12
            year -= 1
        date = str(year) + "-" + '{:02d}'.format(adjusted_month)
        relevant_months.append(date)
    relevant_months.reverse()
    # Current
    relevant_months.append(str(d.year) + "-" + '{:02d}'.format(d.month))
    # After
    year = d.year
    for month in range(d.month + 1, d.month + (months_after + 1)):
        adjusted_month = (month % 12)
        if adjusted_month == 0:
            adjusted_month = 12
        if adjusted_month == 1:
            year += 1
        date = str(year) + "-" + '{:02d}'.format(adjusted_month)
        relevant_months.append(date)

    print(relevant_months)
    return relevant_months


def get_all_coming_movies(max_movies_each_month=10):
    relevant_titles = []
    relevant_months = get_relevant_months()
    for month in relevant_months:
        imdb_page = requests.get('http://www.imdb.com/movies-coming-soon/' + month)
        soup = BeautifulSoup(imdb_page.content, 'html.parser')

        titles_this_month = []
        for link in soup.find_all('h4'):
            if link.get('itemprop') == 'name':
                a = link.contents[0].attrs['title']
                titles_this_month.append(parse_movie_title(link.contents[0].attrs['title']))
                if len(titles_this_month) == max_movies_each_month:
                    break
        relevant_titles += titles_this_month

    print('Titles: ', relevant_titles)
    return relevant_titles


def get_top_100_new_movies_on_itunes():
    titles = []
    itunes_page = requests.get('http://www.apple.com/itunes/charts/movies/')
    soup = BeautifulSoup(itunes_page.content, 'html.parser')
    for link in soup.find_all('a'):
        if 'href' in link.attrs:
            if "https://itunes.apple.com/us/movie/" in link.attrs['href']:
                b = link.contents[0]
                if hasattr(b, 'attrs') and 'alt' in b.attrs:
                    c = b.attrs['alt']
                    titles.append(parse_movie_title(c))
    print(titles)
    return titles

# TODO get more results
def get_new_released_movies_google_play():
    titles = []
    google_play_page = requests.get('https://play.google.com/store/movies/new')
    soup = BeautifulSoup(google_play_page.content, 'html.parser')
    for link in soup.find_all('img'):
        if 'class' in link.attrs:
            if link.attrs['class'][0] == "cover-image":
                c = link.attrs['alt']
                titles.append(c)
    print(titles)
    return titles


def parse_movie_title(title):
    year = datetime.date.today().year
    for i in range(year - 4, year + 5):
        title = title.strip("(" + str(i) + ")")
    return title.strip()


# get_all_coming_movies(max_movies_each_month=5)