package ma.fst.projet_full_stack_2.service;

import ma.fst.projet_full_stack_2.dto.reporting.DashboardDto;
import ma.fst.projet_full_stack_2.dto.reporting.PhaseReportingDto;

import java.time.LocalDate;
import java.util.List;

public interface ReportingService {

    List<PhaseReportingDto> getPhasesBetweenDates(LocalDate dateDebut, LocalDate dateFin);

    List<PhaseReportingDto> getPhasesTermineesNonFacturees(LocalDate dateDebut, LocalDate dateFin);

    List<PhaseReportingDto> getPhasesFactureesNonPayees(LocalDate dateDebut, LocalDate dateFin);

    List<PhaseReportingDto> getPhasesPayees(LocalDate dateDebut, LocalDate dateFin);

    DashboardDto getDashboard();
}
