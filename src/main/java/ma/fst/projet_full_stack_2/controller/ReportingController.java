package ma.fst.projet_full_stack_2.controller;

import ma.fst.projet_full_stack_2.dto.reporting.DashboardDto;
import ma.fst.projet_full_stack_2.dto.reporting.PhaseReportingDto;
import ma.fst.projet_full_stack_2.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/phases")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public List<PhaseReportingDto> getPhasesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return reportingService.getPhasesBetweenDates(dateDebut, dateFin);
    }

    @GetMapping("/phases-terminees-non-facturees")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public List<PhaseReportingDto> getPhasesTermineesNonFacturees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return reportingService.getPhasesTermineesNonFacturees(dateDebut, dateFin);
    }

    @GetMapping("/phases-facturees-non-payees")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public List<PhaseReportingDto> getPhasesFactureesNonPayees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return reportingService.getPhasesFactureesNonPayees(dateDebut, dateFin);
    }

    @GetMapping("/phases-payees")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public List<PhaseReportingDto> getPhasesPayees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return reportingService.getPhasesPayees(dateDebut, dateFin);
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public DashboardDto getDashboard() {
        return reportingService.getDashboard();
    }
}
