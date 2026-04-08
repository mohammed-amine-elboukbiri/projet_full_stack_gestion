package ma.fst.projet_full_stack_2.config;

import lombok.RequiredArgsConstructor;
import ma.fst.projet_full_stack_2.entities.Employe;
import ma.fst.projet_full_stack_2.entities.Organisme;
import ma.fst.projet_full_stack_2.entities.Profil;
import ma.fst.projet_full_stack_2.repository.EmployeRepository;
import ma.fst.projet_full_stack_2.repository.OrganismeRepository;
import ma.fst.projet_full_stack_2.repository.ProfilRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DemoDataSeeder {

    private final OrganismeRepository organismeRepository;
    private final EmployeRepository employeRepository;
    private final ProfilRepository profilRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedDemoData() {
        return args -> {
            Profil chefProjet = ensureProfil("CHEF_PROJET", "Chef de projet");

            syncDemoPassword("admin");
            syncDemoPassword("directeur");
            syncDemoPassword("chef.projet");
            syncDemoPassword("comptable");
            syncDemoPassword("secretaire");
            syncDemoPassword("chef.projet.salma");
            syncDemoPassword("chef.projet.youssef");
            syncDemoPassword("chef.projet.ines");

            seedOrganisme(
                    "ORG-001",
                    "Atlas Industrie",
                    "Technopark Casablanca, route de Nouaceur",
                    "0522000001",
                    "Salma El Idrissi",
                    "contact@atlas-industrie.ma",
                    "https://atlas-industrie.ma"
            );
            seedOrganisme(
                    "ORG-002",
                    "Nova Conseil",
                    "Boulevard Abdelmoumen, Casablanca",
                    "0522000002",
                    "Youssef Bennis",
                    "contact@novaconseil.ma",
                    "https://novaconseil.ma"
            );
            seedOrganisme(
                    "ORG-003",
                    "Maghreb Energie",
                    "Parc industriel Sidi Bernoussi, Casablanca",
                    "0522000003",
                    "Ines Tazi",
                    "contact@maghreb-energie.ma",
                    "https://maghreb-energie.ma"
            );

            seedChefProjet(
                    chefProjet,
                    "CP-001",
                    "El Idrissi",
                    "Salma",
                    "0611000001",
                    "salma.elidrissi@zentask.ma",
                    "chef.projet.salma"
            );
            seedChefProjet(
                    chefProjet,
                    "CP-002",
                    "Bennis",
                    "Youssef",
                    "0611000002",
                    "youssef.bennis@zentask.ma",
                    "chef.projet.youssef"
            );
            seedChefProjet(
                    chefProjet,
                    "CP-003",
                    "Tazi",
                    "Ines",
                    "0611000003",
                    "ines.tazi@zentask.ma",
                    "chef.projet.ines"
            );
        };
    }

    private Profil ensureProfil(String code, String libelle) {
        return profilRepository.findByCode(code)
                .orElseGet(() -> profilRepository.save(
                        Profil.builder()
                                .code(code)
                                .libelle(libelle)
                                .build()
                ));
    }

    private void seedOrganisme(
            String code,
            String nom,
            String adresse,
            String telephone,
            String nomContact,
            String emailContact,
            String siteWeb
    ) {
        if (organismeRepository.findByCode(code).isPresent()) {
            return;
        }

        organismeRepository.save(
                Organisme.builder()
                        .code(code)
                        .nom(nom)
                        .adresse(adresse)
                        .telephone(telephone)
                        .nomContact(nomContact)
                        .emailContact(emailContact)
                        .siteWeb(siteWeb)
                        .build()
        );
    }

    private void seedChefProjet(
            Profil profil,
            String matricule,
            String nom,
            String prenom,
            String telephone,
            String email,
            String login
    ) {
        if (employeRepository.findByLogin(login).isPresent()
                || employeRepository.findByMatricule(matricule).isPresent()
                || employeRepository.findByEmail(email).isPresent()) {
            return;
        }

        employeRepository.save(
                Employe.builder()
                        .matricule(matricule)
                        .nom(nom)
                        .prenom(prenom)
                        .telephone(telephone)
                        .email(email)
                        .login(login)
                        .password(passwordEncoder.encode("admin123"))
                        .profil(profil)
                        .build()
        );
    }

    private void syncDemoPassword(String login) {
        employeRepository.findByLogin(login).ifPresent(employe -> {
            if (passwordEncoder.matches("admin123", employe.getPassword())) {
                return;
            }

            employe.setPassword(passwordEncoder.encode("admin123"));
            employeRepository.save(employe);
        });
    }
}
