package com.arian.vizpotifybackend.services;

import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.Genre;
import com.arian.vizpotifybackend.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public Set<Genre> convertStringArrGenreToGenreObj(String[] genres){
        Set<Genre> output =new HashSet<>();
        for(String genre:genres){
           output.add(new Genre(genre));
        }
        return output;
    }

    public List<String> convertGenresToListString(List<Genre> genres){
        List<String> output = new ArrayList<>();
        for(Genre genre: genres){
            output.add(genre.getName());
        }
        return output;
    }

    public Set<String> convertGenresToSetString(List<Genre> genres){
        Set<String> output = new HashSet<>();
        for(Genre genre: genres){
            output.add(genre.getName());
        }
        return output;
    }

    public void saveNewGenresGivenArtists(Set<ArtistDetail> artistDetailList){
        Set<Genre> allNewGenres = new HashSet<>();
        for(ArtistDetail artistDetail:artistDetailList){
            allNewGenres.addAll(artistDetail.getGenres());
        }

        Set<Genre> existingGenres = genreRepository.findExistingGenres(allNewGenres);
        if(allNewGenres.size()==existingGenres.size()){
            return;
        }
        existingGenres.forEach(allNewGenres::remove);
        genreRepository.saveAll(allNewGenres);
    }

    public void saveNewGenresGivenArtist(ArtistDetail artistDetail){
        Set<Genre> genres = artistDetail.getGenres();
        Set<Genre> existingGenres = genreRepository.findExistingGenres(genres);
        existingGenres.forEach(genres::remove);
        genreRepository.saveAll(genres);
    }
}
