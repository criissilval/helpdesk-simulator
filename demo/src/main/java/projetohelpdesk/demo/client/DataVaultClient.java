package projetohelpdesk.demo.client;
                                                                                                                                                   
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.stereotype.Component;                                                                                                 
  import org.springframework.web.reactive.function.client.WebClient;
  import projetohelpdesk.demo.dto.CustomerResponse;
                                                                                                                                                   
  @Component
  public class DataVaultClient {                                                                                                                   
                  
      private final WebClient webClient;

      public DataVaultClient(@Value("${data-vault.url}") String baseUrl) {
          this.webClient = WebClient.builder()
              .baseUrl(baseUrl)
              .build();                                                                                                                            
      }
                                                                                                                                                   
      public String getCustomerName(String customerId) {
          try {
              return webClient.get()
                  .uri("/customers/{id}", customerId)
                  .retrieve()
                  .bodyToMono(CustomerResponse.class)
                  .map(CustomerResponse::getName)                                                                                                  
                  .block();
          } catch (Exception e) {                                                                                                                  
              return "Cliente nao encontrado";
          }
      }
  }
