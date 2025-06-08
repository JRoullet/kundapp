//package jroullet.msidentity.service;
//
//import jroullet.msidentity.dto.ClientCreateDTO;
//import jroullet.msidentity.dto.ClientResponseDTO;
//import jroullet.msidentity.dto.ClientPatchDTO;
//
//import java.util.List;
//
//public interface ClientService {
//    ClientResponseDTO createClient(ClientCreateDTO clientDTO);
//    ClientResponseDTO getClient(Long id);
//    List<ClientResponseDTO> getAllClients();
//    ClientPatchDTO patchClient(Long id, ClientPatchDTO clientDTO);
//    void disableClient(Long id);
//    void deleteClient(Long id);
//}