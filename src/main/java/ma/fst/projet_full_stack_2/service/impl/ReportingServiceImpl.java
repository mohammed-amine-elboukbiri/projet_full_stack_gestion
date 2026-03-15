package ma.fst.projet_full_stack_2.service.impl;



import ma.fst.projet_full_stack_2.dto.reporting.DashboardDto;
import ma.fst.projet_full_stack_2.dto.reporting.PhaseReportingDto;
import ma.fst.projet_full_stack_2.exception.BadRequestException;
import ma.fst.projet_full_stack_2.mapper.ReportingMapper;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.repository.OrganismeRepository;
import ma.fst.projet_full_stack_2.repository.PhaseRepository;
import ma.fst.projet_full_stack_2.repository.ProjetRepository;
import ma.fst.projet_full_stack_2.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final PhaseRepository phaseRepository;
    private final ProjetRepository projetRepository;
    private final EmployeRepository employeRepository;
    private final OrganismeRepository organismeRepository;
    private final ReportingMapper reportingMapper;

    @Override
    public List<PhaseReportingDto> getPhasesBetweenDates(LocalDate dateDebut, LocalDate dateFin) {
        validateDates(dateDebut, dateFin);
        return phaseRepository.findByDateFinBetween(dateDebut, dateFin)
                .stream()
                .map(reportingMapper::toDto)
                .toList();
    }

    @Override
    public List<PhaseReportingDto> getPhasesTermineesNonFacturees(LocalDate dateDebut, LocalDate dateFin) {
        validateDates(dateDebut, dateFin);
        return phaseRepository
                .findByDateFinBetweenAndEtatRealisationTrueAndEtatFacturationFalse(dateDebut, dateFin)
                .stream()
                .map(reportingMapper::toDto)
                .toList();
    }

    @Override
    public List<PhaseReportingDto> getPhasesFactureesNonPayees(LocalDate dateDebut, LocalDate dateFin) {
        validateDates(dateDebut, dateFin);
        return phaseRepository
                .findByDateFinBetweenAndEtatFacturationTrueAndEtatPaiementFalse(dateDebut, dateFin)
                .stream()
                .map(reportingMapper::toDto)
                .toList();
    }

    @Override
    public List<PhaseReportingDto> getPhasesPayees(LocalDate dateDebut, LocalDate dateFin) {
        validateDates(dateDebut, dateFin);
        return phaseRepository
                .findByDateFinBetweenAndEtatPaiementTrue(dateDebut, dateFin)
                .stream()
                .map(reportingMapper::toDto)
                .toList();
    }

    @Override
    public DashboardDto getDashboard() {
        return DashboardDto.builder()
                .totalProjets(projetRepository.count())
                .totalPhases(phaseRepository.count())
                .totalEmployes(employeRepository.count())
                .totalOrganismes(organismeRepository.count())
                .phasesTerminees(phaseRepository.countByEtatRealisationTrue())
                .phasesNonFacturees(phaseRepository.countByEtatRealisationTrueAndEtatFacturationFalse())
                .phasesFactureesNonPayees(phaseRepository.countByEtatFacturationTrueAndEtatPaiementFalse())
                .phasesPayees(phaseRepository.countByEtatPaiementTrue())
                .build();
    }

    private void validateDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new BadRequestException("Les dates sont obligatoires");
        }
        if (dateDebut.isAfter(dateFin)) {
            throw new BadRequestException("La date de début ne peut pas être postérieure à la date de fin");
        }
    }
}