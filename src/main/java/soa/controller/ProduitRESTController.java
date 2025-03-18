package soa.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import soa.entities.Produit;
import soa.entities.Categorie;
import soa.repository.ProduitRepository;
import soa.repository.CategorieRepository;

@RestController()
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/produits")
public class ProduitRESTController {

    @Autowired
    private ProduitRepository produitRepos;

    @Autowired
    private CategorieRepository categorieRepos;

    // Welcome message
    @GetMapping(value = "/index")
    public String accueil() {
        return "BienVenue au service Web REST 'produits'.....";
    }

    // Get all products
    @GetMapping(value = "/", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public List<Produit> getAllProduits() {
        return produitRepos.findAll();
    }

    // Get a product by ID
    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Produit getProduit(@PathVariable Long id) {
        return produitRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    // Delete a product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitRepos.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        produitRepos.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Add a new product
    @PostMapping(value = "/add", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Produit saveProduit(@RequestBody Produit p) {
        if (p.getCategorie() != null && p.getCategorie().getId() != null) {
            Categorie category = categorieRepos.findById(p.getCategorie().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + p.getCategorie().getId()));
            p.setCategorie(category);
        } else {
            throw new RuntimeException("Category ID must be provided");
        }
        return produitRepos.save(p);
    }
    @PostMapping(value = "/categories/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Categorie addCategorie(@RequestBody Categorie categorie) {
        return categorieRepos.save(categorie);
    }
    // Update an existing product
    @PutMapping(value = "/update", produces = { MediaType.APPLICATION_JSON_VALUE })
    public Produit updateProduit(@RequestBody Produit p) {
        Produit existingProduit = produitRepos.findById(p.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + p.getId()));

        // Update fields
        existingProduit.setCode(p.getCode());
        existingProduit.setDesignation(p.getDesignation());
        existingProduit.setPrix(p.getPrix());
        existingProduit.setQuantite(p.getQuantite());
        existingProduit.setDateAchat(p.getDateAchat());
        existingProduit.setEnPromotion(p.isEnPromotion());

        if (p.getCategorie() != null && p.getCategorie().getId() != null) {
            Categorie category = categorieRepos.findById(p.getCategorie().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + p.getCategorie().getId()));
            existingProduit.setCategorie(category);
        } else {
            throw new RuntimeException("Category ID must be provided");
        }

        return produitRepos.save(existingProduit);
    }

    // Get all categories
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Categorie> getAllCategories() {
        return categorieRepos.findAll();
    }

    // Update product category
    @PutMapping(value = "/update-category/{produitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Produit updateProductCategory(@PathVariable Long produitId, @RequestBody Long categoryId) {
        Produit produit = produitRepos.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + produitId));
        Categorie category = categorieRepos.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
        produit.setCategorie(category);
        return produitRepos.save(produit);
    }

    // Filter products
    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Produit> filterProduits(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) Double prixMin,
            @RequestParam(required = false) Double prixMax,
            @RequestParam(required = false) Integer quantiteMin,
            @RequestParam(required = false) Integer quantiteMax,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateAchat,
            @RequestParam(required = false) Long categorie) {

        Specification<Produit> spec = Specification.where(null);

        if (code != null) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("code"), "%" + code + "%"));
        }
        if (designation != null) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("designation"), "%" + designation + "%"));
        }
        if (prixMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("prix"), prixMin));
        }
        if (prixMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("prix"), prixMax));
        }
        if (quantiteMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("quantite"), quantiteMin));
        }
        if (quantiteMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("quantite"), quantiteMax));
        }
        if (dateAchat != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("dateAchat"), dateAchat));
        }
        if (categorie != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categorie").get("id"), categorie));
        }

        return produitRepos.findAll(spec);
    }
}