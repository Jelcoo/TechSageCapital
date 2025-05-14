package com.techsage.banking.controllers;

import com.techsage.banking.exceptions.*;
import com.techsage.banking.models.*;
import com.techsage.banking.models.dto.*;
import com.techsage.banking.models.dto.requests.*;
import com.techsage.banking.models.dto.responses.*;
import com.techsage.banking.models.enums.*;
import com.techsage.banking.services.*;
import com.techsage.banking.services.interfaces.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.web.bind.annotation.*;

import javax.naming.*;
import javax.naming.AuthenticationException;
import java.util.*;

@RestController
@RequestMapping("/atm")
@Tag(name = "ATM", description = "Endpoints for ATM operations")
public class AtmController extends BaseController {

}
