from flask import Flask, request
import collect_movies
import json
import threading
app = Flask(__name__)

COLLECTION_INTERVAL = 24*60*60.0

movie_interrest_dict = {}
already_notified_dict = {}
movies_out = set()
movies_not_out = set()

relevant_movies_of_today = []
itunes_movies_of_today = []
google_play_movies_of_today = []

def do_collection():
    print("Collecting")
    global relevant_movies_of_today, itunes_movies_of_today, google_play_movies_of_today
    global movies_out, movies_not_out
    relevant_movies_of_today = collect_movies.get_all_coming_movies(max_movies_each_month=5)
    movies_not_out |= set(relevant_movies_of_today)

    itunes_movies_of_today = collect_movies.get_top_100_new_movies_on_itunes()
    google_play_movies_of_today = collect_movies.get_new_released_movies_google_play()
    movies_out |= set(itunes_movies_of_today)
    movies_out |= set(google_play_movies_of_today)

do_collection()
threading.Timer(COLLECTION_INTERVAL, do_collection).start()


@app.route("/movies-coming-soon/<username>", methods=['GET'])
def get_relevant_movies(username):
    if username not in movie_interrest_dict:
        movie_interrest_dict[username] = set()
    not_already_favorited_movies = set(relevant_movies_of_today).difference(movie_interrest_dict[username])
    return json.dumps(list(not_already_favorited_movies))


@app.route('/favorite', methods=['POST'])
def set_interrest_in_movie():
    username = request.form['username']
    movie = request.form['movie']
    if username in movie_interrest_dict:
        movie_interrest_dict[username].add(movie)
    else:
        movie_interrest_dict[username] = set(movie)
    return json.dumps(list(movie_interrest_dict[username]))


@app.route("/itunes-movies/")
def get_itunes_movies():
    return json.dumps(list(itunes_movies_of_today))

@app.route("/google-play-movies/")
def get_google_play_movies():
    return json.dumps(list(google_play_movies_of_today))

@app.route('/user/<username>')
def get_user_movies(username):
    if username not in movie_interrest_dict:
        movie_interrest_dict[username] = set()
    return json.dumps(list(movie_interrest_dict[username]))


@app.route('/whats-new/<username>')
def whats_new(username):
    global already_notified_dict, movie_interrest_dict, movies_out
    if username not in movie_interrest_dict:
        movie_interrest_dict[username] = set()
    new_and_interresting_to_user = movie_interrest_dict[username].intersection(movies_out)
    if username not in already_notified_dict:
        already_notified_dict[username] = set()
    not_notified_about = new_and_interresting_to_user.difference(already_notified_dict[username])
    already_notified_dict[username] |= not_notified_about
    return json.dumps(list(not_notified_about))


if __name__ == "__main__":
    app.run(host='0.0.0.0')
    # app.run(host='192.168.1.100')
    # app.run(host='192.168.1.14', port=80, debug=True)
