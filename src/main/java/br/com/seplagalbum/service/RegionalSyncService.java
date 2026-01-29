package br.com.seplagalbum.service;

import br.com.seplagalbum.dto.RegionalExternalDTO;
import br.com.seplagalbum.model.Regional;
import br.com.seplagalbum.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalSyncService {

    private final RegionalRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://integrador-argus-api.geia.vip/v1/regionais";

    @Scheduled(fixedRate = 3600000) // Sincroniza a cada hora
    public void syncRegionais() {
        try {
            RegionalExternalDTO[] externalArray = restTemplate.getForObject(API_URL, RegionalExternalDTO[].class);
            if (externalArray == null) return;

            List<RegionalExternalDTO> externalList = Arrays.asList(externalArray);
            List<Regional> internalList = repository.findByAtivoTrue();

            Map<Integer, Regional> internalMap = internalList.stream()
                    .collect(Collectors.toMap(Regional::getId, r -> r));

            Set<Integer> externalIds = externalList.stream()
                    .map(RegionalExternalDTO::getId)
                    .collect(Collectors.toSet());

            // 1. Novo no endpoint ou alterado -> inserir/inativar antigo
            for (RegionalExternalDTO ext : externalList) {
                Regional internal = internalMap.get(ext.getId());
                if (internal == null) {
                    // Novo no endpoint -> inserir
                    repository.save(new Regional(null, ext.getId(), ext.getNome(), true));
                    log.info("Inserida nova regional: {}", ext.getNome());
                } else {
                    // Existe internamente. Verificar se mudou
                    if (!internal.getNome().equals(ext.getNome())) {
                        // Atributo alterado -> inativar antigo e criar novo registro (conforme requisito iii-3)
                        internal.setAtivo(false);
                        repository.save(internal);
                        
                        repository.save(new Regional(null, ext.getId(), ext.getNome(), true));
                        log.info("Regional alterada. Inativada antiga e criada nova: {}", ext.getNome());
                    }
                }
            }

            // 2. Ausente no endpoint -> inativar
            for (Regional internal : internalList) {
                if (!externalIds.contains(internal.getId())) {
                    internal.setAtivo(false);
                    repository.save(internal);
                    log.info("Regional ausente no endpoint. Inativada: {}", internal.getNome());
                }
            }

        } catch (Exception e) {
            log.error("Erro ao sincronizar regionais", e);
        }
    }
}
