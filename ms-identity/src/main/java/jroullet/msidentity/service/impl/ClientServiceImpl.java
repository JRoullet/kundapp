//package jroullet.msidentity.service.impl;
//
//import jroullet.msidentity.dto.ClientCreateDTO;
//import jroullet.msidentity.dto.ClientPatchDTO;
//import jroullet.msidentity.dto.ClientResponseDTO;
//import jroullet.msidentity.mapper.ClientMapper;
//import jroullet.msidentity.model.Role;
//import jroullet.msidentity.model.User;
//import jroullet.msidentity.repository.UserRepository;
//import jroullet.msidentity.service.ClientService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class ClientServiceImpl implements ClientService {
//
//    private final static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
//    private final UserRepository userRepository;
//    private final ClientMapper clientMapper;
//
//    @Override
//    @Transactional
//    public ClientResponseDTO createClient(ClientCreateDTO clientDTO) {
//        logger.info("Creating new client with email: {}", clientDTO.getEmail());
//
//        if (userRepository.existsByEmail(clientDTO.getEmail())) {
//            throw new IllegalArgumentException("Email already exists");
//        }
//
//        ClientProfile client = clientMapper.toEntity(clientDTO);
//        client.setRole(Role.CLIENT);
//        client.setStatus(true);
//
//        ClientProfile savedClient = userRepository.save(client);
//        return clientMapper.toResponseDTO(savedClient);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ClientResponseDTO getClient(Long id) {
//        logger.info("Getting client with id: {}", id);
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
//
//        if (!(user instanceof ClientProfile)) {
//            throw new IllegalArgumentException("User is not a client");
//        }
//
//        return clientMapper.toResponseDTO((ClientProfile) user);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<ClientResponseDTO> getAllClients() {
//        logger.info("Getting all clients");
//
//        return userRepository.findByRole(Role.CLIENT).stream()
//                .map(user -> clientMapper.toResponseDTO((ClientProfile) user))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional
//    public ClientPatchDTO patchClient(Long id, ClientPatchDTO clientDTO) {
//        logger.info("Patching client with id: {}", id);
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
//
//        if (!(user instanceof ClientProfile client)) {
//            throw new IllegalArgumentException("User is not a client");
//        }
//
//        clientMapper.updateEntityFromDTO(clientDTO, client);
//
//        ClientProfile updatedClient = (ClientProfile) userRepository.save(client);
//        return clientMapper.toPatchDTO(updatedClient);
//    }
//
//    @Override
//    @Transactional
//    public void disableClient(Long id) {
//        logger.info("Disabling client with id: {}", id);
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
//
//        if (!(user instanceof ClientProfile)) {
//            throw new IllegalArgumentException("User is not a client");
//        }
//
//        user.setStatus(false);
//        userRepository.save(user);
//    }
//
//    @Override
//    @Transactional
//    public void deleteClient(Long id) {
//        logger.info("Deleting client with id: {}", id);
//
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
//
//        if (!(user instanceof ClientProfile)) {
//            throw new IllegalArgumentException("User is not a client");
//        }
//
//        userRepository.delete(user);
//    }
//}