package DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import classes.DBConnection;
import classes.Equipe;
import modeles.TournoiModele;

public class TournoiDAO extends SingletonDAO {
	
	public TournoiDAO() {
		super();
	}
	
	//Renvois l'ensemble des arbitres
	public List<TournoiModele> getAll() throws Exception {
		ArrayList<TournoiModele> tournois = new ArrayList<>();
		String reqSelectTournoi = "SELECT * FROM tournoi";
		PreparedStatement st = DBConnection.getInstance().prepareStatement(reqSelectTournoi);
		ResultSet rs = st.executeQuery();
		String reqSelectParticipants = "SELECT idEquipe FROM Participation WHERE idTournoi = ?";
		PreparedStatement stParticipants = DBConnection.getInstance().prepareStatement(reqSelectParticipants);
		ArrayList<Equipe> participants = new ArrayList<>();
		while (rs.next()) {
			TournoiModele t = new TournoiModele(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), classes.Notoriete.valueOf(rs.getString(5)), classes.EtatTournoi.valueOf(rs.getString(7)));
			stParticipants.setInt(1, rs.getInt(1));
			ResultSet rsParticipants = stParticipants.executeQuery();
			while (rsParticipants.next()) {
				t.ajouterEquipe(null);
			}
			tournois.add(t);
			participants.clear();
		}
		return tournois;
	}
	
	//retourne un Arbitre specifique
	public Optional<TournoiModele> getById(Integer... id) throws Exception {
		Statement st = DBConnection.getInstance().createStatement();
		for (Integer i : id) {
			ResultSet rs = st.executeQuery("SELECT * FROM tournoi WHERE idTournoi="+i);
			if (rs.next()) {
				TournoiModele t = new TournoiModele(rs.getInt(1), rs.getString(2), rs.getString(3),rs.getString(4), classes.Notoriete.valueOf(rs.getString(5)), classes.EtatTournoi.valueOf(rs.getString(7)));
				PreparedStatement stParticipants = DBConnection.getInstance().prepareStatement("SELECT idEquipe FROM Participer WHERE idTournoi = ?");
				stParticipants.setInt(1, rs.getInt(1));
				ResultSet rsParticipants = stParticipants.executeQuery();
				while (rsParticipants.next()) {
					t.ajouterEquipe(null);
				}
				return Optional.of(t);
			}
		}
		return Optional.empty();
	}
	
	//ajoute un arbitre à la liste
	public boolean add(TournoiModele value) throws Exception {
		
		PreparedStatement st = DBConnection.getInstance().prepareStatement("SELECT NEXT VALUE FOR seqIdTournoi FROM admin");
		ResultSet rs = st.executeQuery();
		int id = 0;
		if (rs.next()) {
			id = rs.getInt(1);
		}
		value.setIDTournoi(id);
		st = DBConnection.getInstance().prepareStatement("INSERT INTO tournoi VALUES (?, ?, ?, ?, ?, ?, ?)");
		st.setInt(1, id); 
		st.setString(2, value.getNomTournoi());
		st.setDate(3, value.getDateDebut()); 
		st.setDate(4, value.getDateFin());
		st.setString(5, value.getNotoriete().toString());
		st.setString(6, value.getEtatTournoi().toString());
		st.setObject(7, null);
		int rowcount = st.executeUpdate();
		return rowcount > 0;
	}
	
	//update un arbitre donné
	public boolean update(TournoiModele value) throws Exception {
		Statement st = DBConnection.getInstance().createStatement();
		int rowcount = st.executeUpdate("UPDATE tournoi SET ");
		return rowcount > 0;
	}
	
	//retire un arbitre donné
	public boolean delete(TournoiModele value) throws Exception {
		PreparedStatement st = DBConnection.getInstance().prepareStatement("DELETE FROM tournoi WHERE idTournoi=?");
		st.setInt(1, value.getIDTournoi());
		//Statement st = DBConnection.getInstance().createStatement();
		int rowcount = st.executeUpdate();
		return rowcount > 0;
	}
	
	public Optional<TournoiModele> getTournoiOuvert() throws Exception {
		Statement st = DBConnection.getInstance().createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM tournoi WHERE ouvert='OUVERT'");
		if (rs.next()) {
			return Optional.of(new TournoiModele(rs.getInt(1), "", rs.getString(2), rs.getString(3), 
					classes.Notoriete.valueOf(rs.getString(3)), classes.EtatTournoi.valueOf(rs.getString(4))));
		}
		return Optional.empty();
	}

}
