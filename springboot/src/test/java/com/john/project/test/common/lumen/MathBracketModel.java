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

    private void handleAdd(MathBracketModel addModel) {
        handleAdd(addSymbol, addModel, this);
    }

    private void handleSubtract(MathBracketModel addModel) {
        handleAdd(subtractSymbol, addModel, this);
    }

    @SneakyThrows
    private boolean handleAdd(String addCalculationSymbol, MathBracketModel addModel, MathBracketModel parent) {
        var objectMapper = SpringUtil.getBean(ObjectMapper.class);

        if (!List.of(addSymbol, subtractSymbol).contains(addCalculationSymbol)) {
            return false;
        }

        if (ObjectUtil.equals(equalSymbol, calculationSymbol)) {
            if (!childOne.handleMultiply(addModel, this)) {
                setChildOne(new MathBracketModel()
                        .setChildOne(childOne)
                        .setCalculationSymbol(addCalculationSymbol)
                        .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(addModel), MathBracketModel.class))
                );
            }
            if (!childTwo.handleMultiply(addModel, this)) {
                setChildTwo(new MathBracketModel()
                        .setChildOne(childTwo)
                        .setCalculationSymbol(addCalculationSymbol)
                        .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(addModel), MathBracketModel.class))
                );
            }
        } else {
            if (ObjectUtil.equals(childTwo.toString(), addModel.toString()) && ObjectUtil.equals(addCalculationSymbol, calculationSymbol) && ObjectUtil.equals(parent.getChildOne().toString(), this.toString())) {
                parent.setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(childOne), MathBracketModel.class));
                return true;
            } else if (ObjectUtil.equals(childTwo.toString(), addModel.toString()) && ObjectUtil.equals(addCalculationSymbol, calculationSymbol) && ObjectUtil.equals(parent.getChildTwo().toString(), this.toString())) {
                parent.setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(childOne), MathBracketModel.class));
                return true;
            } else if (ObjectUtil.equals(childOne.toString(), addModel.toString()) && ObjectUtil.equals(addSymbol, calculationSymbol) && List.of(addSymbol, equalSymbol).contains(parent.getCalculationSymbol()) && ObjectUtil.equals(subtractSymbol, addCalculationSymbol) && ObjectUtil.equals(parent.getChildOne().toString(), this.toString())) {
                parent.setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(childTwo), MathBracketModel.class));
                return true;
            }  else if (ObjectUtil.equals(childOne.toString(), addModel.toString()) && ObjectUtil.equals(addSymbol, calculationSymbol) && List.of(addSymbol, equalSymbol).contains(parent.getCalculationSymbol()) && ObjectUtil.equals(subtractSymbol, addCalculationSymbol) && ObjectUtil.equals(parent.getChildTwo().toString(), this.toString())) {
                parent.setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(childTwo), MathBracketModel.class));
                return true;
            } else {
                if (childOne.handleMultiply(addModel, this)) {
                    return true;
                } else if (childTwo.handleMultiply(addModel, this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void handleAddToMultiply(MathBracketModel addModel) {
        this.handleAddToMultiply(addModel, this);
    }

    @SneakyThrows
    private boolean handleAddToMultiply(MathBracketModel addModel, MathBracketModel parent) {
        var objectMapper = SpringUtil.getBean(ObjectMapper.class);

        if (StringUtils.isNotBlank(name)) {
            return false;
        }
        if (!List.of(addSymbol, subtractSymbol).contains(addModel.getCalculationSymbol())) {
            return false;
        }

        if (ObjectUtil.equals(equalSymbol, calculationSymbol)) {
            while (childOne.handleAddToMultiply(addModel, this)) {
            }
            while (childTwo.handleAddToMultiply(addModel, this)) {
            }
            return false;
        }

        if (ObjectUtil.equals(multiplySymbol, calculationSymbol) && ObjectUtil.equals(multiplySymbol, parent.getCalculationSymbol()) && List.of(childOne.toString(), childTwo.toString()).contains(addModel.toString())) {
            parent
                    .setChildTwo(
                            new MathBracketModel()
                                    .setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(ObjectUtil.equals(addModel.toString(), childOne.toString()) ? childTwo : childOne), MathBracketModel.class))
                                    .setCalculationSymbol(multiplySymbol)
                                    .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(ObjectUtil.equals(this.toString(), parent.childOne.toString()) ? parent.childTwo : parent.childOne), MathBracketModel.class))
                    )
                    .setCalculationSymbol(multiplySymbol)
                    .setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(addModel), MathBracketModel.class));
            return true;
        }

        if (ObjectUtil.equals(multiplySymbol, calculationSymbol)) {
            if (ObjectUtil.equals(childOne.toString(), addModel.toString())) {
                this.setChildOne(
                        new MathBracketModel()
                                .setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(addModel.getChildOne()), MathBracketModel.class))
                                .setCalculationSymbol(multiplySymbol)
                                .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(childTwo), MathBracketModel.class))
                );
                this.setCalculationSymbol(addModel.getCalculationSymbol());
                this.setChildTwo(
                        new MathBracketModel()
                                .setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(addModel.getChildTwo()), MathBracketModel.class))
                                .setCalculationSymbol(multiplySymbol)
                                .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(childTwo), MathBracketModel.class))
                );
                return true;
            } else if (ObjectUtil.equals(childTwo.toString(), addModel.toString())) {
                this.setChildTwo(
                        new MathBracketModel()
                                .setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(childOne), MathBracketModel.class))
                                .setCalculationSymbol(multiplySymbol)
                                .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(addModel.getChildTwo()), MathBracketModel.class))

                );
                this.setChildOne(
                        new MathBracketModel()
                                .setChildOne(objectMapper.readValue(objectMapper.writeValueAsString(childOne), MathBracketModel.class))
                                .setCalculationSymbol(multiplySymbol)
                                .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(addModel.getChildOne()), MathBracketModel.class))

                );
                this.setCalculationSymbol(addModel.getCalculationSymbol());
                return true;
            }
        }
        if (childOne.handleAddToMultiply(addModel, this)) {
            return true;
        }
        if (childTwo.handleAddToMultiply(addModel, this)) {
            return true;
        }
        return false;
    }

    public void handleMultiply(MathBracketModel multipleModel) {
        this.handleMultiply(multipleModel, null);
    }

    @SneakyThrows
    private boolean handleMultiply(MathBracketModel multipleModel, MathBracketModel parent) {
        if (StringUtils.isNotBlank(name)) {
            return false;
        }
        if (List.of(addSymbol, subtractSymbol).contains(calculationSymbol)) {
            return false;
        }

        var objectMapper = SpringUtil.getBean(ObjectMapper.class);
        if (ObjectUtil.equals(equalSymbol, calculationSymbol)) {
            if (!childOne.handleMultiply(multipleModel, this)) {
                setChildOne(new MathBracketModel()
                        .setChildOne(childOne)
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(multipleModel), MathBracketModel.class))
                );
            }
            if (!childTwo.handleMultiply(multipleModel, this)) {
                setChildTwo(new MathBracketModel()
                        .setChildOne(childTwo)
                        .setCalculationSymbol(multiplySymbol)
                        .setChildTwo(objectMapper.readValue(objectMapper.writeValueAsString(multipleModel), MathBracketModel.class))
                );
            }
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
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        if (ObjectUtil.isNotNull(childOne) && ObjectUtil.isNotNull(childTwo) && StringUtils.isNotBlank(calculationSymbol)) {
            var hasBracketSymbolOfChildOne = !(List.of(addSymbol, multiplySymbol).contains(calculationSymbol) && ObjectUtil.equals(calculationSymbol, childOne.getCalculationSymbol()));
            var hasBracketSymbolOfChildTwo = !(List.of(addSymbol, multiplySymbol).contains(calculationSymbol) && ObjectUtil.equals(calculationSymbol, childTwo.getCalculationSymbol()));
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
