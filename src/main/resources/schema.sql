create TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

create TABLE IF NOT EXISTS genres (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

create TABLE IF NOT EXISTS users (
    user_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    birthday DATE NOT NULL
);

create TABLE IF NOT EXISTS films (
    film_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_id INTEGER,
    FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id)
);

create TABLE IF NOT EXISTS film_genre (
    film_id INTEGER,
    genre_id INTEGER,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON delete CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(genre_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS friendship (
    user_id INTEGER,
    friend_id INTEGER,
    status BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON delete CASCADE
);

create TABLE IF NOT EXISTS likes (
    film_id INTEGER,
    user_id INTEGER,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON delete CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON delete CASCADE
);