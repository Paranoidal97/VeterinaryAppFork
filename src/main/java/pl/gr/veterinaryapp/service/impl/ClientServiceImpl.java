package pl.gr.veterinaryapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.gr.veterinaryapp.exception.IncorrectDataException;
import pl.gr.veterinaryapp.exception.ResourceNotFoundException;
import pl.gr.veterinaryapp.mapper.ClientMapper;
import pl.gr.veterinaryapp.model.dto.ClientRequestDto;
import pl.gr.veterinaryapp.model.dto.ClientResponseDto;
import pl.gr.veterinaryapp.model.entity.Client;
import pl.gr.veterinaryapp.model.entity.VetAppUser;
import pl.gr.veterinaryapp.repository.ClientRepository;
import pl.gr.veterinaryapp.repository.UserRepository;
import pl.gr.veterinaryapp.service.ClientService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper mapper;
    private final UserRepository userRepository;

    @Override
    public ClientResponseDto getClientById(Long id) {
        System.out.println("XXX");
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
        return mapper.toDto(client);
    }

    @Override
    public List<ClientResponseDto> getAllClients() {

        List<Client> clients = clientRepository.findAll();
        return  clients.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ClientResponseDto createClient(ClientRequestDto clientRequestDTO) {
        if (clientRequestDTO.getSurname() == null || clientRequestDTO.getName() == null) {
            throw new IncorrectDataException("Name and Surname should not be null.");
        }

        VetAppUser user = userRepository.findByUsername(clientRequestDTO.getUsername())
                .orElse(null);

        Client client = mapper.toEntity(clientRequestDTO);
        client.setUser(user);

        clientRepository.save(client);
        return mapper.toDto(client);
    }

    @Transactional
    @Override
    public void deleteClient(Long id) {
        Client result = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wrong id."));
        clientRepository.delete(result);
    }
}
