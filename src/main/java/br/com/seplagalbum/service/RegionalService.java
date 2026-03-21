package br.com.seplagalbum.service;

import br.com.seplagalbum.exception.ResourceNotFoundException;
import br.com.seplagalbum.model.Regional;
import br.com.seplagalbum.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionalService {

    private final RegionalRepository repository;

    public List<Regional> listar(boolean apenasAtivas) {
        return apenasAtivas ? repository.findByAtivoTrue() : repository.findAll();
    }

    public Regional buscarPorInternalId(Long internalId) {
        return repository.findById(internalId)
                .orElseThrow(() -> new ResourceNotFoundException("Regional não encontrada com id: " + internalId));
    }
}
