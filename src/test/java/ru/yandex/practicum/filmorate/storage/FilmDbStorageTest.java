package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.LikeDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, GenreDao.class, LikeDao.class})
class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmDbStorage;

    @Test
    public void testCreateAndGetFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Film createdFilm = filmDbStorage.create(film);

        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("Film");

        Film expectedFilm = new Film();
        expectedFilm.setId(createdFilm.getId());
        expectedFilm.setName("Film");
        expectedFilm.setDescription("Description");
        expectedFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        expectedFilm.setDuration(120);

        Mpa expectedMpa = new Mpa();
        expectedMpa.setId(1);
        expectedMpa.setName("G");
        expectedFilm.setMpa(expectedMpa);
        expectedFilm.setLikes(Collections.emptySet());
        expectedFilm.setGenres(Collections.emptySet());

        Film retrievedFilm = filmDbStorage.getById(createdFilm.getId());
        assertThat(retrievedFilm).isEqualTo(expectedFilm);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film1.setMpa(mpa);

        Film film2 = new Film();
        film2.setName("Film 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(130);
        film2.setMpa(mpa);

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);

        Collection<Film> films = filmDbStorage.getAll();
        assertThat(films).hasSize(2);
    }
}
