package ma.fst.projet_full_stack_2.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AffectationId implements Serializable {

    private Long employeId;
    private Long phaseId;
}
