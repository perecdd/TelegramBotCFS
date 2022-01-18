package io.swagger.api;

import io.swagger.DataBase;
import io.swagger.model.Product;
import io.swagger.model.Ticket;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-01-18T15:05:10.280Z[GMT]")
@RestController
public class UserApiController implements UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public UserApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Product>> getUserBasket(@Parameter(in = ParameterIn.HEADER, description = "User's email" ,required=true,schema=@Schema()) @RequestHeader(value="email", required=true) String email,@Parameter(in = ParameterIn.HEADER, description = "User's password" ,required=true,schema=@Schema()) @RequestHeader(value="password", required=true) String password) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Product>>(objectMapper.readValue(CFS.getBasket(email, password).toString(), List.class), HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Product>>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<List<Product>>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> getUserLogin(@Parameter(in = ParameterIn.HEADER, description = "User's email" ,required=true,schema=@Schema()) @RequestHeader(value="email", required=true) String email,@Parameter(in = ParameterIn.HEADER, description = "User's password" ,required=true,schema=@Schema()) @RequestHeader(value="password", required=true) String password) {
        String accept = request.getHeader("Accept");
        if(CFS.tryLogin(email, password)) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity<List<Ticket>> getUserTickets(@Parameter(in = ParameterIn.HEADER, description = "User's email" ,required=true,schema=@Schema()) @RequestHeader(value="email", required=true) String email,@Parameter(in = ParameterIn.HEADER, description = "User's password" ,required=true,schema=@Schema()) @RequestHeader(value="password", required=true) String password) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Ticket>>(objectMapper.readValue(ShopOwnerSide.getTickets(email, password).toString(), List.class), HttpStatus.OK);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Ticket>>(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<List<Ticket>>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> postUserTickets(@Parameter(in = ParameterIn.HEADER, description = "User's email" ,required=true,schema=@Schema()) @RequestHeader(value="email", required=true) String email,@Parameter(in = ParameterIn.HEADER, description = "User's password" ,required=true,schema=@Schema()) @RequestHeader(value="password", required=true) String password,@Parameter(in = ParameterIn.HEADER, description = "Order ID." ,required=true,schema=@Schema()) @RequestHeader(value="id", required=true) Integer id) {
        String accept = request.getHeader("Accept");
        if(ShopOwnerSide.postTicket(email, password, id)) {
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
    }

}
