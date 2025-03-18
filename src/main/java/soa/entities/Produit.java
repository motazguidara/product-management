package soa.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Produit {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String code;
    
    @Column(length = 50)
    private String designation;

    private double prix;
    private int quantite;

    // Use LocalDate instead of Date
    @JsonFormat(pattern = "yyyy-MM-dd")  // Optional: Ensures the date format is consistent during serialization/deserialization
    private LocalDate dateAchat;

    private boolean enPromotion;

    @ManyToOne
    @JsonIgnoreProperties("produits")
    private Categorie categorie;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private Collection<Stock> stocks = new ArrayList<Stock>();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public boolean isEnPromotion() {
        return enPromotion;
    }

    public void setEnPromotion(boolean enPromotion) {
        this.enPromotion = enPromotion;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Collection<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(Collection<Stock> stocks) {
        this.stocks = stocks;
    }

    @Override
    public String toString() {
        return "Produit [id=" + id + ", code=" + code + ", designation=" + designation +
                ", prix=" + prix + ", quantite=" + quantite + ", dateAchat=" + dateAchat +
                ", enPromotion=" + enPromotion + ", categorie=" + categorie + ", stocks=" + stocks + "]";
    }

    // Constructors
    public Produit(String code, String designation, double prix, int quantite, LocalDate dateAchat, Categorie categorie) {
        this.code = code;
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.categorie = categorie;
    }

    public Produit(String designation, double prix, int quantite, LocalDate dateAchat, Categorie categorie) {
        this.designation = designation;
        this.prix = prix;
        this.quantite = quantite;
        this.dateAchat = dateAchat;
        this.categorie = categorie;
    }

    public Produit() {
        super();
    }
}
