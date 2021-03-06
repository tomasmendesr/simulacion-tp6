package simulacion;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

public class Main {
	
	public static DateTime HV = new DateTime(2600, 9, 21, 9, 0,0);
	public static int MAX = 1000000;

	public static void main(String[] args) {
		Tps[] tps; // Tiempo proxima salida (i)
		DateTime tpll; // Teimpo proxima llegada
        long[] sto; // Sumatoria tiempo ocioso (i)
        double[] pto; // Porcentaje tiempo ocioso (i)
        long staa; // Sumatoria tiempo atencion Alta prioridad
        long stab; // Sumatoria tiempo de atencion Baja prioridad
        double ppsa; // Promedio permanencia en el sistema cola Alta prioridad
        double ppsb; // Promedio permanencia en el sitema cola baja prioridad
        double peca; // Promedio espera cola A
        double pecb; // Promedio espera cola B
        long stlla;  // Sumatoria tiempo llegada cola A
        long stllb; // Sumatoria tiempo llegada cola B
        DateTime[] ito; // Inicio tiempo ocioso (i)
        long stsa; // Sumatoria tiempo salida A
        long stsb; // Sumatoria tiempo salida B
        int ns; // Cantidad de elementos en la cola de Baja prioridad
        int np; // Cantidad de elementos en la cola de Alta prioridad
        DateTime t, tf, tInicial; // Tiempo inicial, tiempo final;
        int puestos;
		int i;
		int nta; // Contador de elementos para A
		int ntb; // Contador de elementos para B
        
        // Condiciones iniciales
		puestos = Integer.parseInt(Config.getInstance().getProperty("puestos"));
		boolean calcularEnBaseAlTotal = Config.getInstance().getProperty("calcularEnBaseAlTotal").toString().equals("si");
		t = new DateTime(2017, 9, 10, 9, 0,0);
		tInicial = t;
		tf = new DateTime(2017, 9, 20, 12, 0,0);
		np = 0;
		ns = 0;
		nta = 0;
		ntb = 0;
		stlla = 0;
		stllb = 0;
		stsb = 0;
		stsa = 0;
		staa = 0;
		stab = 0;
		tps = new Tps[puestos];
		sto = new long[puestos]; 
        ito = new DateTime[puestos]; 
        pto = new double[puestos];
        for(int j = 0;j<puestos;j++){
        	pto[j] = 0;
        	sto[j] = 0;
        	tps[j] = new Tps();
        	tps[j].setTime(HV);
        }
		tpll = t;
		boolean quedanElementos = true;
		
		while( t.isBefore(tf) || quedanElementos){
			// TPLL <= TPS(i)
			i = buscarMenorTPS(tps);
			if(tpll.isBefore(tps[i].getTime())){
				// LLEGADA
				t = tpll;
				tpll = tpll.plusMinutes(obtenerIntervaloEntreArribos());
				double random = random();
				if(random <= 0.6){
					// LLEAGADA A COLA DE BAJA PRIORIDAD
					ns++;
					ntb++;
					stllb = stllb + TimeUnit.MILLISECONDS.toMinutes(t.getMillis());
					if(ns + np <= puestos){
						long ta = obtenerTiempoDeAtencion();
						stab = stab + ta;
						int x = buscarTpsHV(tps);
						tps[x].setTime(t.plusMinutes((int) ta));
						tps[x].setPrioridadAlta(false);
						if(ito[x] != null) sto[x] = sto[x] + (long) diffInMinutes(t, ito[x]);
					}
				}else{
					// LLEGADA A COLA DE ALTA PRIORIDAD
					np++;
					nta++;
					stlla = stlla + TimeUnit.MILLISECONDS.toMinutes(t.getMillis());
					if(ns + np <= puestos){
						long ta = obtenerTiempoDeAtencion();
						staa = staa + ta;
						int x = buscarTpsHV(tps);
						tps[x].setTime(t.plusMinutes((int) ta));
						tps[x].setPrioridadAlta(true);
						if(ito[x] != null) sto[x] = (long) (sto[x] + diffInMinutes(t, ito[x]));
					}
				}
			}else{
				// SALIDA (i)
				t = tps[i].getTime();
				if(tps[i].getPrioridadAlta()){
					// Se van de la cola de baja prioridad
					// Se va de la cola de alta prioridad
					np--;
					stsa = stsa + TimeUnit.MILLISECONDS.toMinutes(t.getMillis());
				}else{
					ns--;
					stsb = stsb + TimeUnit.MILLISECONDS.toMinutes(t.getMillis());
				}
				if(ns + np >= puestos){
					long ta = obtenerTiempoDeAtencion();
					tps[i].setTime(t.plusMinutes((int) ta));
					int puestosAtendiendoNp = 0;
					for(int z = 0;z<tps.length;z++){
						if(z != i){
							if(tps[z].getPrioridadAlta()) puestosAtendiendoNp++;
						}
					}
					if(np > puestosAtendiendoNp){
						staa = staa + ta;
						tps[i].setPrioridadAlta(true);
					}else{
						tps[i].setPrioridadAlta(false);
						stab = stab + ta;
					}
				}else{
					ito[i] = t;
					tps[i].setTime(HV);
				}
			}
			
			if(t.isAfter(tf)){
				if(ns + np == 0){
					quedanElementos = false;
				}else{
					tpll = HV;
				}
			}
		}
		
		System.out.println("Con " + puestos + " puestos");
		System.out.println("Llegaron " + nta + " tickets de Alta prioridad");
		System.out.println("Llegaron " + ntb + " tickets de Baja prioridad");
		int nt = nta + ntb;
		if(calcularEnBaseAlTotal){
			ppsa = nta != 0 ? (stsa - stlla) / nt : 0;
			peca = nta != 0 ? (stsa - stlla - staa) / nt : 0;
			ppsb = ntb != 0 ? (stsb - stllb) / nt : 0;
			pecb = ntb != 0 ? (stsb - stllb - stab) / nt : 0;
		}else{
			ppsa = nta != 0 ? (stsa - stlla) / nta : 0;
			peca = nta != 0 ? (stsa - stlla - staa) / nta : 0;
			ppsb = ntb != 0 ? (stsb - stllb) / ntb : 0;
			pecb = ntb != 0 ? (stsb - stllb - stab) / ntb : 0;
		}
		double minTotales = diffInMinutes(tf, tInicial);
		DecimalFormat df = new DecimalFormat("#0.00");
		for(int k = 0;k<puestos;k++){
			if(ito[k] != null){
				pto[k] = sto[k] != 0 ? (sto[k] / minTotales) * 100 : 0;
			}else{
				pto[k] = 100;
			}
			System.out.println("Porcentaje tiempo ocioso puesto #" + (k + 1) + ": " + df.format(pto[k]) + "%");
		}
		System.out.println("Promedio permanencia sistema A: " + Math.round(ppsa) + " minutos");
		System.out.println("Promedio permanencia sistema B: " + Math.round(ppsb) + " minutos");
		System.out.println("Promedio espera cola A: " + Math.round(peca) + " minutos");
		System.out.println("Promedio espera cola B: " + Math.round(pecb) + " minutos");

	}
	
	private static double diffInMinutes(DateTime  tf, DateTime  tInicial) {
		return TimeUnit.MILLISECONDS.toMinutes(tf.getMillis() - tInicial.getMillis());
	}

	private static Integer buscarTpsHV(Tps[] tps) {
		Integer result = null;
		for(int i = 0;i<tps.length;i++){
			if(tps[i].getTime() == HV){
				result = i;
				break;
			}
		}
		return result;
	}

	private static long obtenerTiempoDeAtencion() {
		double r = random();
		if(r <= 0.55){
			return obtenerTAComplejidadBaja();
		}else if( r <= 85){
			return obtenerTAComplejidadMedia();
		}else{
			return obtenerTAComplejidadAlta();
		}
	}
	
	private static long obtenerTAComplejidadAlta(){
		double result = MAX;
		for(;;){
			double r = random();
			result = Math.sqrt(170 * r) + 58; 
			if(result >= 58 && result <= 63){
				return Math.round(result);
			}else{
				result = 92 - Math.sqrt((1-r)*986);
				if(result >= 63 && result <= 92){
					return Math.round(result);
				}
			}
		}
	}
	
	private static long obtenerTAComplejidadMedia(){
		double r = random();
		return Math.round((long) (30 * r + 28));
	}
	
	private static long obtenerTAComplejidadBaja(){
		long result = MAX;
		while(result < 10 || result > 30){
			double r = random();
			result = Math.round((Math.log(1 - r) / (-0.05)));
		}
		return result;
	}

	private static double random(){
		Random r = new Random();
		return r.nextDouble();
	}

	private static int obtenerIntervaloEntreArribos() {
		double result = MAX;
		while(result > 36){
			double random = random();
			result = 13 / (Math.sqrt(1 - random));
		}
		return (int) Math.round(result);
	}

	private static int buscarMenorTPS(Tps[] tps) {
		int result = 0;
		for(int i = 0;i<tps.length;i++){
			if(tps[i].getTime().isBefore(tps[result].getTime())){
				result = i;
			}
		}
		return result;
	}

}
