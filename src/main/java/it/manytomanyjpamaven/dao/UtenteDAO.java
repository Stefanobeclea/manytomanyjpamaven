package it.manytomanyjpamaven.dao;

import java.util.List;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteDAO extends IBaseDAO<Utente> {
	
	public List<Utente> findAllByRuolo(Ruolo ruoloInput);
	public Utente findByIdFetchingRuoli(Long id);
	
	public List<Utente> findAllByUtentiCreatiInData () throws Exception;
	
	public Long countNumeroUtentiAdmin () throws Exception;
	
	public List<Utente> findAllByUtentiConPasswordPiuCortaDi () throws Exception;
	
	public boolean FoundSeTraUtentiDisabilitatiEPresenteUnAdmin () throws Exception;

}
