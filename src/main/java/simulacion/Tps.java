package simulacion;

import java.time.LocalDateTime;

public class Tps {
	private LocalDateTime time;
	private Boolean prioridadAlta;
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public Boolean getPrioridadAlta() {
		return prioridadAlta;
	}
	public void setPrioridadAlta(Boolean prioridadAlta) {
		this.prioridadAlta = prioridadAlta;
	}

}
