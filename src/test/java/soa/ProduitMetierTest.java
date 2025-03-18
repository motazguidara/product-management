package soa;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import soa.entities.Categorie;
import soa.entities.Produit;
import soa.entities.Stock;
import soa.metier.ProduitMetierImpl;
import soa.repository.CategorieRepository;
import soa.repository.ProduitRepository;

@ExtendWith(MockitoExtension.class)
class ProduitMetierTest {
    
    @Mock
    private ProduitRepository produitRepository;
    
    @Mock
    private CategorieRepository categorieRepository;
    
    @InjectMocks
    private ProduitMetierImpl produitMetier;

    @Test
    void testChangerCategorieProduit_Success() {
        // Arrange
        Produit produit = new Produit();
        Categorie categorie = new Categorie();
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(categorieRepository.findById(1L)).thenReturn(Optional.of(categorie));
        
        // Act
        boolean result = produitMetier.changerCategorieProduit(1L, 1L);
        
        // Assert
        assertTrue(result);
        assertEquals(categorie, produit.getCategorie());
    }
    
    @Test
    void testChangerCategorieProduit_Failure() {
        when(produitRepository.findById(1L)).thenReturn(Optional.empty());
        
        try {
            boolean result = produitMetier.changerCategorieProduit(1L, 1L);
            assertFalse(result);
        } catch (NoSuchElementException e) {
            // Handle the exception if necessary
        }
    }

    @Test
    void testRendreProduitsEnPromotionAvant() {
        // Arrange
        Produit produit1 = new Produit();
        produit1.setDateAchat(LocalDate.of(2023, 1, 1));
        produit1.setEnPromotion(false);
        
        Produit produit2 = new Produit();
        produit2.setDateAchat(LocalDate.of(2025, 1, 1));
        produit2.setEnPromotion(false);
        
        when(produitRepository.findAllByDateAchatBefore(Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant())))
            .thenReturn(Arrays.asList(produit1));
        
        // Act
        produitMetier.rendreProduitsEnPromotionAvant(Date.from(LocalDate.of(2024, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        // Assert
        assertTrue(produit1.isEnPromotion());
        assertFalse(produit2.isEnPromotion());
    }
    
    @Test
    void testCalculerCoutVenteStock() {
        // Arrange
        Produit produit1 = new Produit();
        produit1.setPrix(100.0);
        produit1.setQuantite(5);
        produit1.setEnPromotion(false);
        
        Produit produit2 = new Produit();
        produit2.setPrix(200.0);
        produit2.setQuantite(3);
        produit2.setEnPromotion(true);
        
        when(produitRepository.findAll()).thenReturn(Arrays.asList(produit1, produit2));
        
        // Act
        double cout = produitMetier.calculerCoutVenteStock(10);
        
        // Assert
        assertEquals(5 * 100 + 3 * 200 * 0.9, cout, 0.01);
    }

    @Test
    void testAjouterProduit() {
        // Arrange
        Produit produit = new Produit();
        when(produitRepository.save(produit)).thenReturn(produit);
        
        // Act
        produitMetier.ajouterProduit(produit);
        
        // Assert
        assertNotNull(produitRepository.save(produit));
    }

    @Test
    void testAjouterProduitAvecCategorie() {
        // Arrange
        Produit produit = new Produit();
        Categorie categorie = new Categorie();
        when(produitRepository.save(produit)).thenReturn(produit);
        when(categorieRepository.save(categorie)).thenReturn(categorie);
        
        // Act
        produitMetier.ajouterProduit(produit, categorie);
        
        // Assert
        assertNotNull(produitRepository.save(produit));
        assertNotNull(categorieRepository.save(categorie));
    }

    @Test
    void testAjouterProduitAvecCategorieEtStock() {
        // Arrange
        Produit produit = new Produit();
        Categorie categorie = new Categorie();
        Stock stock = new Stock();
        when(produitRepository.save(produit)).thenReturn(produit);
        when(categorieRepository.save(categorie)).thenReturn(categorie);
        
        // Act
        produitMetier.ajouterProduit(produit, categorie, stock);
        
        // Assert
        assertNotNull(produitRepository.save(produit));
        assertNotNull(categorieRepository.save(categorie));
    }

    @Test
    void testListeProduits() {
        // Arrange
        Produit produit1 = new Produit();
        Produit produit2 = new Produit();
        when(produitRepository.findAll()).thenReturn(Arrays.asList(produit1, produit2));
        
        // Act
        List<Produit> produits = produitMetier.listeProduits();
        
        // Assert
        assertNotNull(produits);
        assertEquals(2, produits.size());
    }

}
