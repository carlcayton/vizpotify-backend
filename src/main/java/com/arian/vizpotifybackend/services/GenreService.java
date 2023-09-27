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


    public Set<String> convertGenresToSetString(Set<Genre> genres){
        return genres.stream().map(Genre::getName).collect(Collectors.toSet());
    }

    public void saveNewGenresGivenArtists(Set<ArtistDetail> artistDetailList){
        Set<Genre> allNewGenres= artistDetailList.stream()
                .flatMap(artistDetail -> artistDetail.getGenres().stream())
                .collect(Collectors.toSet());

        Set<String> allNewGenresAsString = allNewGenres
                .stream().map(Genre::getName).collect(Collectors.toSet());
        Set<String> existingGenresNames = genreRepository.findExistingGenres(allNewGenresAsString);

        if(allNewGenresAsString.size() == existingGenresNames.size()){
            return;
        }

        allNewGenres.removeIf(genre -> existingGenresNames.contains(genre.getName()));
        genreRepository.saveAll(allNewGenres);
    }

    public void saveNewGenresGivenArtist(ArtistDetail artistDetail){
        Set<Genre> allNewGenres= artistDetail.getGenres();
        Set<String> existingGenres = genreRepository.findExistingGenres(convertGenresToSetString(allNewGenres));
        if(allNewGenres.size() == existingGenres.size()){
            return;
        }
        allNewGenres.removeIf(genre -> existingGenres.contains(genre.getName()));
        genreRepository.saveAll(allNewGenres);
    }
}
