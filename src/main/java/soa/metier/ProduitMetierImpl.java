package soa.metier;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import soa.entities.Categorie;
import soa.entities.Produit;
import soa.entities.Stock;
import soa.repository.CategorieRepository;
import soa.repository.ProduitRepository;

@Service
public class ProduitMetierImpl implements ProduitMetierInterface {

    @Autowired
    ProduitRepository produitRepository;
    @Autowired
    CategorieRepository categorieRepository;

    @Override
    public boolean changerCategorieProduit(Long idProduit, Long idNouvelleCategorie) {
        Produit p = produitRepository.findById(idProduit).orElse(null);
        Categorie c = categorieRepository.findById(idNouvelleCategorie).orElse(null);
        if (p != null && c != null) {
            p.setCategorie(c);
            produitRepository.save(p);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void ajouterProduit(Produit p) {
        produitRepository.save(p);
    }

    @Override
    public void ajouterProduit(Produit p, Categorie c) {
        categorieRepository.save(c);
        p.setCategorie(c);
        produitRepository.save(p);
    }

    @Override
    public void ajouterProduit(Produit p, Categorie c, Stock s) {
        categorieRepository.save(c);
        p.setCategorie(c);
        p.getStocks().add(s);
        produitRepository.save(p);
    }

    @Override
    public List<Produit> listeProduits() {
        return produitRepository.findAll();
    }

    @Override
    public void rendreProduitsEnPromotionAvant(Date date) {
        List<Produit> lp = produitRepository.findAllByDateAchatBefore(date);
        for (Produit p : lp) {
            p.setEnPromotion(true);
            produitRepository.save(p);
        }
    }

    @Override
    public double calculerCoutVenteStock(double coefficientRemise) {
        double cout = 0.0;
        List<Produit> lp = produitRepository.findAll();
        for (Produit p : lp) {
            double prix = !p.isEnPromotion() ? p.getPrix() : p.getPrix() * (1 - coefficientRemise / 100);
            cout += p.getQuantite() * prix;
        }
        return cout;
    }
}
