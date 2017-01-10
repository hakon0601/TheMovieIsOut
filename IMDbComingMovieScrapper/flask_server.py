from flask import Flask, request
import collect_movies
import json
app = Flask(__name__)

movie_interrest_dict = {}
already_notified_dict = {}
movies_out = set()

@app.route("/movies-coming-soon/", methods=['GET'])
def get_relevant_movies():
    return json.dumps(collect_movies.get_all_coming_movies(max_movies_each_month=5))


@app.route('/favorite', methods=['POST'])
def set_interrest_in_movie():
    username = request.form['username']
    movie = request.form['movie']
    if username in movie_interrest_dict:
        movie_interrest_dict[username].append(movie)
    else:
        movie_interrest_dict[username] = [movie]
    return json.dumps(movie_interrest_dict[username])


@app.route("/itunes-movies/")
def get_itunes_movies():
    global movies_out
    itunes_movies_out = collect_movies.get_top_100_new_movies_on_itunes()
    movies_out |= set(itunes_movies_out)
    return json.dumps(list(movies_out))


@app.route('/user/<username>')
def get_user_movies(username):
    return json.dumps(movie_interrest_dict[username])


@app.route('/whats-new/<username>')
def whats_new(username):
    movies_set = set()
    itunes_movies_set = set(collect_movies.get_top_100_new_movies_on_itunes())
    google_play_set = set(collect_movies.get_new_released_movies_google_play())
    movies_set |= itunes_movies_set
    movies_set |= google_play_set
    if username not in movie_interrest_dict:
        movie_interrest_dict[username] = []
    new_and_interresting_to_user = set(movie_interrest_dict[username]).intersection(movies_set)
    if username not in already_notified_dict:
        already_notified_dict[username] = set()
    not_notified_about = new_and_interresting_to_user.difference(already_notified_dict[username])
    already_notified_dict[username] |= not_notified_about
    return json.dumps(list(not_notified_about))


if __name__ == "__main__":
    app.run(host='0.0.0.0')
    # app.run(host='192.168.1.100')
    # app.run(host='192.168.1.14', port=80, debug=True)
