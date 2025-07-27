package com.john.project.test.common.lumen;


import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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

    public boolean handleMultiply(MathBracketModel multipleModel) {
        return this.handleMultiply(multipleModel, null);
    }

    @SneakyThrows
    private boolean handleMultiply(MathBracketModel multipleModel, MathBracketModel parent) {
        if (StringUtils.isNotBlank(name)) {
            return false;
        }

        var objectMapper = SpringUtil.getBean(ObjectMapper.class);
        if (ObjectUtil.equals(equalSymbol, calculationSymbol)) {
            if (!childOne.handleMultiply(multipleModel, this)) {
                setChildOne(new MathBracketModel()
                        .setChildOne(childOne)
                        .setCalculationSymbol(multipleSymbol)
                        .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(multipleModel), MathBracketModel.class))
                );
            }
            if (!childTwo.handleMultiply(multipleModel, this)) {
                setChildTwo(new MathBracketModel()
                        .setChildOne(childTwo)
                        .setCalculationSymbol(multipleSymbol)
                        .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(multipleModel), MathBracketModel.class))
                );
            }

            return true;
        } else {
            if (ObjectUtil.equals(childTwo.toString(), multipleModel.toString()) && ObjectUtil.equals(divideSymbol, calculationSymbol)) {
                if (ObjectUtil.equals(parent.getChildOne().toString(), this.toString())) {
                    parent.setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(childOne), MathBracketModel.class));
                } else if (ObjectUtil.equals(parent.getChildTwo().toString(), this.toString())) {
                    parent.setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(childOne), MathBracketModel.class));
                }
                return true;
            } else {
                if (childOne.handleMultiply(multipleModel, this)) {
                    return true;
                } else if (childTwo.handleMultiply(multipleModel, this)) {
                    return true;
                }
                return false;
            }
        }
    }

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
