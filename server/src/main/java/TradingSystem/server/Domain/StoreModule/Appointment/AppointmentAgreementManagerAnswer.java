package TradingSystem.server.Domain.StoreModule.Appointment;

import TradingSystem.server.DAL.HibernateUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AppointmentAgreementManagerAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean has_answer;
    private boolean answer;

    public boolean get_has_answer() {
        return has_answer;
    }

    public void setHas_answer(boolean has_answer) {
        this.has_answer = has_answer;
//        HibernateUtils.merge(this);
    }

    public boolean get_answer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
//        HibernateUtils.merge(this);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isHas_answer() {
        return has_answer;
    }

    public boolean isAnswer() {
        return answer;
    }
}
