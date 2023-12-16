package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.AnimalMapper;
import pl.gr.veterinaryapp.model.dto.AnimalRequestDto;
import pl.gr.veterinaryapp.model.entity.Animal;
import pl.gr.veterinaryapp.repository.AnimalRepository;
import pl.gr.veterinaryapp.service.AnimalService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AnimalServiceImpl implements AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalMapper mapper;

    @Override
    public AnimalRequestDto getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
        return mapper.toDto(animal);
    }

    @Transactional
    @Override
    public AnimalRequestDto createAnimal(AnimalRequestDto animalRequestDto) {
        var animal = animalRepository.findBySpecies(animalRequestDto.getSpecies());
        if (animal.isPresent()) {
            throw new IncorrectDataException("Species exists.");
        }

         animalRepository.save(mapper.toEntity(animalRequestDto));
        return animalRequestDto;
    }

    @Transactional
    @Override
    public void deleteAnimal(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
        animalRepository.delete(animal);
    }

    @Override
    public List<AnimalRequestDto> getAllAnimals() {

        List<Animal> animals = animalRepository.findAll();

        return animals.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
