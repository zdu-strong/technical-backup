package com.john.project.test.common.lumen;


import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.john.project.test.common.lumen.LumenMathTest.*;

@Getter
@Setter
@Accessors(chain = true)
public class MathBracketModel {

    private String id;

    private String name;

    private MathBracketModel childOne;

    private String calculationSymbol;

    private MathBracketModel childTwo;

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        if (ObjectUtil.isNotNull(childOne) && ObjectUtil.isNotNull(childTwo) && StringUtils.isNotBlank(calculationSymbol)) {
            var hasBracketSymbolOfChildOne = !(List.of(addSymbol, multipleSymbol).contains(calculationSymbol) && ObjectUtil.equals(calculationSymbol, childOne.getCalculationSymbol()));
            var hasBracketSymbolOfChildTwo = !(List.of(addSymbol, multipleSymbol).contains(calculationSymbol) && ObjectUtil.equals(calculationSymbol, childTwo.getCalculationSymbol()));
            if (StringUtils.isNotBlank(childOne.getName())) {
                hasBracketSymbolOfChildOne = false;
            }
            if (StringUtils.isNotBlank(childTwo.getName())) {
                hasBracketSymbolOfChildTwo = false;
            }
            if (StringUtils.equals(equalSymbol, calculationSymbol)) {
                hasBracketSymbolOfChildOne = false;
                hasBracketSymbolOfChildTwo = false;
            }
            return StrFormatter.format(
                    "{}{}{} {} {}{}{}",
                    Optional.of(hasBracketSymbolOfChildOne).filter(s -> s).map(s -> "(").orElse(StringUtils.EMPTY),
                    childOne.toString(),
                    Optional.of(hasBracketSymbolOfChildOne).filter(s -> s).map(s -> ")").orElse(StringUtils.EMPTY),
                    calculationSymbol,
                    Optional.of(hasBracketSymbolOfChildTwo).filter(s -> s).map(s -> "(").orElse(StringUtils.EMPTY),
                    childTwo.toString(),
                    Optional.of(hasBracketSymbolOfChildTwo).filter(s -> s).map(s -> ")").orElse(StringUtils.EMPTY)
            );
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no match model");
    }

}
