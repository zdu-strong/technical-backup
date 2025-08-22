package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.john.project.common.baseController.BaseController;

import static com.john.project.constant.HelloWorldConstant.HELLO_WORLD;

@RestController
public class HelloWorldController extends BaseController {

	@GetMapping("/")
	public ResponseEntity<?> helloWorld() {
		return ResponseEntity.ok(HELLO_WORLD);
	}

}
