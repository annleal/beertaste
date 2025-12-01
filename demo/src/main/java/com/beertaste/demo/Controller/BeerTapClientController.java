package com.beertaste.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/beertap/view")
public class BeerTapClientController {

    @GetMapping
    public String clientViewPage() {
        return "beertap-view"; // carga templates/beertap-view.html
    }
}
