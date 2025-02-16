package com.example.demo.controller;

import com.example.demo.model.Entrenador;
import com.example.demo.model.Pokemon;
import com.example.demo.repository.EntrenadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EntrenadorController {

	@Autowired
	private EntrenadorRepository entrenadorRepository;

	@GetMapping({ "/entrenadores", "/" })
	public String listarEntrenadores(Model modelo) {
	    List<Entrenador> entrenadores = entrenadorRepository.findAll();
	   
	    modelo.addAttribute("entrenadores", entrenadores);
	    return "entrenadores";
	}

	@GetMapping("/entrenadores/crear")
	public String formularioCrear(Model modelo) {
		Entrenador entrenador = new Entrenador();
		modelo.addAttribute("entrenador", entrenador);
		return "crearEntrenador";
	}

	@PostMapping("/entrenadores")
	public String guardarEntrenador(@ModelAttribute("entrenador") Entrenador entrenador,
			@RequestParam List<String> pokemons) {
		for (String nombrePokemon : pokemons) {
			Pokemon pokemon = new Pokemon();
			pokemon.setNombre(nombrePokemon);
			pokemon.setNivel(5);
			pokemon.setSalud(100);
			pokemon.setPoderAtaque(20);
			pokemon.setEntrenador(entrenador);

			entrenador.getEquipo().add(pokemon);
		}

		entrenadorRepository.save(entrenador);

		return "redirect:/entrenadores";
	}

	@GetMapping("/entrenadores/editar/{id}")
	public String formularioEditar(@PathVariable Long id, Model modelo) {
		Entrenador entrenador = entrenadorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

		modelo.addAttribute("entrenador", entrenador);
		modelo.addAttribute("pokemons", entrenador.getEquipo());

		return "editarEntrenador";
	}

	@PostMapping("/entrenadores/{id}")
	public String actualizarEntrenador(@PathVariable Long id, @ModelAttribute("entrenador") Entrenador entrenador,
	                                   @RequestParam List<String> pokemonsNames, @RequestParam List<Integer> pokemonsLevels) {
	    Entrenador entrenadorExistente = entrenadorRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

	    entrenadorExistente.setNombre(entrenador.getNombre());
	    entrenadorExistente.setNivel(entrenador.getNivel());

	    for (int i = 0; i < pokemonsNames.size(); i++) {
	        String nombrePokemon = pokemonsNames.get(i);
	        Integer nivelPokemon = pokemonsLevels.get(i);

	        if (!nombrePokemon.isEmpty()) {
	            boolean existePokemon = false;

	            for (Pokemon p : entrenadorExistente.getEquipo()) {
	                if (p.getNombre().equals(nombrePokemon)) {
	                    existePokemon = true;
	                    p.setNivel(nivelPokemon != null ? nivelPokemon : 5);	                    // No se hace  break porque puede haber más Pokémon con el mismo nombre (aunque en este caso no debería haberlo)
	                }
	            }

	            if (!existePokemon) {
	                Pokemon pokemon = new Pokemon();
	                pokemon.setNombre(nombrePokemon);
	                pokemon.setNivel(nivelPokemon != null ? nivelPokemon : 5);
	                pokemon.setSalud(100);
	                pokemon.setPoderAtaque(20);
	                pokemon.setEntrenador(entrenadorExistente);

	                entrenadorExistente.getEquipo().add(pokemon);
	            }
	        }
	    }

	    entrenadorRepository.save(entrenadorExistente);

	    return "redirect:/entrenadores";
	}


	@GetMapping("/entrenadores/{id}")
	public String eliminarEntrenador(@PathVariable Long id) {
		entrenadorRepository.deleteById(id);
		return "redirect:/entrenadores";
	}

	@GetMapping("/entrenadores/detalles/{id}")
    public String detallesEntrenador(@PathVariable Long id, Model modelo) {
        Entrenador entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        modelo.addAttribute("entrenador", entrenador);
        return "detallesEntrenador"; 
    }
}
