package it.manytomanyjpamaven.test;

import java.util.Date;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testRimuoviUtente(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
						
			testRimuoviRuolo(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testCercaTuttiUtentiCreatiInData(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testContaNumeroUtentiAdmin(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testCercaTuttiUtentiConPasswordPiuCortaDi(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testFoundSeTraUtentiDisabilitatiEPresenteUnAdmin(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testCercaTutteDescrizioneDeiRuoliConUtentiAssociati(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}
	
	private static void testRimuoviUtente (UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception{
		System.out.println(".......testRimuoviUtente inizio.............");
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		//Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		//utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		
		//utenteServiceInstance.rimuoviRuoloDaUtente(utenteNuovo, ruoloEsistenteSuDb);
		
		Long idDaCheccare = utenteNuovo.getId();
		utenteServiceInstance.rimuovi(utenteNuovo);
		if(utenteServiceInstance.caricaSingoloElemento(idDaCheccare) != null) {
			throw new RuntimeException("testRimuoviUtente fallito: rimozione non avvenuta correttamente ");
		}
		System.out.println(".......testRimuoviUtente fine: PASSED.............");
	}
	
	private static void testRimuoviRuolo (UtenteService utenteServiceInstance,RuoloService ruoloServiceInstance) throws Exception{
		System.out.println(".......testRimuoviRuolo inizio.............");
		Ruolo ruoloDaCancellare = new Ruolo("Priv User", "ROLE_PRIV_USER");
		ruoloServiceInstance.inserisciNuovo(ruoloDaCancellare);
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		//Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		//utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		
		ruoloServiceInstance.rimuovi(ruoloServiceInstance.caricaSingoloElemento(ruoloDaCancellare.getId()));
		if(ruoloServiceInstance.caricaSingoloElemento(ruoloDaCancellare.getId()) != null) {
			throw new RuntimeException("testRimuoviRuolo fallito: rimozione non avvenuta correttamente ");
		}
		System.out.println(".......testRimuoviRuolo fine: PASSED.............");
	}
	
	private static void testCercaTuttiUtentiCreatiInData (UtenteService utenteServiceInstance) throws Exception{
		System.out.println(".......testCercaTuttiUtentiCreatiInData inizio.............");
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date ("2021/06/06"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		//Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		//utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		
		if(utenteServiceInstance.cercaTuttiUtentiCreatiInData().size() != 1) {
			throw new RuntimeException("testCercaTuttiUtentiCreatiInData fallito:  ");
		}
		utenteServiceInstance.rimuovi(utenteNuovo);
		System.out.println(".......testCercaTuttiUtentiCreatiInData fine: PASSED.............");
	}
	
	private static void testContaNumeroUtentiAdmin (UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception{
		System.out.println(".......testContaNumeroUtentiAdmin inizio.............");
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date ("2021/10/06"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		if(utenteServiceInstance.contaNumeroUtentiAdmin() == 0) {
			throw new RuntimeException("testContaNumeroUtentiAdmin fallito:  ");
		}
		System.out.println(".......testContaNumeroUtentiAdmin fine: PASSED.............");
	}
	
	private static void testCercaTuttiUtentiConPasswordPiuCortaDi (UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception{
		System.out.println(".......testCercaTuttiUtentiConPasswordPiuCortaDi inizio.............");
		Utente utenteNuovo = new Utente("mario1.bianchi1", "jj", "mario1", "bianchi1", new Date ("2021/10/06"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if(utenteServiceInstance.cercaTuttiUtentiConPasswordPiuCortaDi().size() != 1) {
			throw new RuntimeException("testCercaTuttiUtentiConPasswordPiuCortaDi fallito:  ");
		}
		utenteServiceInstance.rimuovi(utenteNuovo);
		System.out.println(".......testCercaTuttiUtentiConPasswordPiuCortaDi fine: PASSED.............");
	}
	
	private static void testFoundSeTraUtentiDisabilitatiEPresenteUnAdmin (UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception{
		System.out.println(".......testFoundSeTraUtentiDisabilitatiEPresenteUnAdmin inizio.............");
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date ("2021/10/06"));
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		utenteNuovo.setStato(StatoUtente.DISABILITATO);
		utenteServiceInstance.aggiorna(utenteNuovo);
		if(!utenteServiceInstance.trovaSeTraUtentiDisabilitatiEPresenteUnAdmin()) {
			throw new RuntimeException("testFoundSeTraUtentiDisabilitatiEPresenteUnAdmin fallito:  ");
		}
		System.out.println(".......testFoundSeTraUtentiDisabilitatiEPresenteUnAdmin fine: PASSED.............");
	}
	
	private static void testCercaTutteDescrizioneDeiRuoliConUtentiAssociati (UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception{
		System.out.println(".......testCercaTutteDescrizioneDeiRuoliConUtentiAssociati inizio.............");
		
		if(ruoloServiceInstance.cercaTutteDescrizioneDeiRuoliConUtentiAssociati() == null) {
			throw new RuntimeException("testCercaTutteDescrizioneDeiRuoliConUtentiAssociati fallito:  ");
		}
		System.out.println(".......testCercaTutteDescrizioneDeiRuoliConUtentiAssociati fine: PASSED.............");
	}
}
