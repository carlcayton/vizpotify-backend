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

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> convertStringArrGenreToGenreObj(String[] genres){
        List<Genre> output = new ArrayList<>();
        for(String genre:genres){


           output.add(new Genre(genre));
        }
        return output;
    }

    public void saveNewGenresGivenArtists(List<ArtistDetail> artistDetailList){
        Set<String> allNewGenres = new HashSet<>();
        for(ArtistDetail artistDetail:artistDetailList){
            artistDetail.getGenres().forEach(genre -> allNewGenres.add(genre.getName()));
        }

        List<String> existingGenres = genreRepository.findExistingGenres(allNewGenres);
        if(allNewGenres.size()==existingGenres.size()){
            return;
        }
        existingGenres.forEach(allNewGenres::remove);
        List<Genre> newGenresToSave = convertStringArrGenreToGenreObj(allNewGenres.toArray(new String[allNewGenres.size()]));
        genreRepository.saveAll(newGenresToSave);
    }


}
