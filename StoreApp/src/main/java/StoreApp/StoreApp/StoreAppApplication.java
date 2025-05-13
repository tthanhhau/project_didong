package StoreApp.StoreApp;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.cloudinary.Cloudinary;

@SpringBootApplication
public class StoreAppApplication {

	@Value("${cloudinary.cloud_name}")
	private String cloudName;


	
	public static void main(String[] args) {
		SpringApplication.run(StoreAppApplication.class, args);

	}
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
	
	@Bean
	public Cloudinary cloudinaryConfig() {
		Cloudinary cloudinary = null;
		Map<String, String> config = new HashMap<String, String>();
		config.put("cloud_name", cloudName);
		cloudinary = new Cloudinary(config);
		return cloudinary;
	}
}
