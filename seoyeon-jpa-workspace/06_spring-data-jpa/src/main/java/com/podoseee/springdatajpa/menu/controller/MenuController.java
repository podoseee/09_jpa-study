package com.podoseee.springdatajpa.menu.controller;

import com.podoseee.springdatajpa.menu.dto.CategoryDto;
import com.podoseee.springdatajpa.menu.dto.MenuDto;
import com.podoseee.springdatajpa.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/menu")
@Controller
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/{menuCode}")
    public String menuDetail(@PathVariable int menuCode, Model model){
        MenuDto menu = menuService.findMenuByCode(menuCode);
        model.addAttribute("menu", menu);

        return "menu/detail";
    }

    /*
        ## Pageable ##
        1. 페이징 처리에 필요한 정보(page, size, sort)를 처리하는 인터페이스
        2. Pageable 객체를 통해서 페이징 처리와 정렬을 동시에 처리할 수 있음
        3. 사용방법
            1) 페이징 처리에 필요한 정보를 따로 파라미터 전달받아 직접 생성하는 방법
               PageRequest.of(요청페이지번호, 조회할데이터건수, Sort객체)
            2) 정해진 파라미터(page, size, sort)로 전달받아 생성된 객체 바로 주입하는 방법
                @PageableDefault Pageable Pageable
                => 따로 전달된 파라미터가 존재하지 않을 경우 기본값
        4. 주의사항
            Pageable 인터페이스는 조회할 페이지번호를 0부터 인식
            => 넘어오는 페이지번호 파라미터를 -1 해야됨
     */

    // /menu/list?[page=xx]&[size=xx]&[sort=xx, asc|desc]
    // 페이징 후
    @GetMapping("/list")
    public String menuList(@PageableDefault Pageable pageable, Model model){  // <-- Model 추가

        log.info("pageable: {}", pageable); // Pageable 매개변수에 page, size, sort 자동으로 바인딩 됨

        // * withPage() : 현재 Pageable의 기존설정(페이지size, 정렬 등)은 그대로 두고, 페이지 번호만 바꾼 '새로운 Pageable 객체를 반환'
        pageable = pageable.withPage(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1);

        if(pageable.getSort().isEmpty()){ // 정렬 파라미터가 존재하지 않을 경우 => 기본 정렬 기준 세우기
            // 정렬만 바꾸는 건 따로 존재하지 않음 => 다시 새로 생성해야됨
            pageable = PageRequest.of(pageable.getPageNumber()
                                    , pageable.getPageSize()
                                    , Sort.by("menuCode").descending());
        }

        log.info("변경후 pageable: {}", pageable);

        Map<String, Object> map = menuService.findMenuList(pageable);
        model.addAttribute("menuList", map.get("menuList"));
        model.addAttribute("page", map.get("page"));
        model.addAttribute("beginPage", map.get("beginPage"));
        model.addAttribute("endPage", map.get("endPage"));
        model.addAttribute("isFirst", map.get("isFirst"));
        model.addAttribute("isLast", map.get("isLast"));

        List<CategoryDto> categoryList = menuService.findCategoryList();
        model.addAttribute("categoryList", categoryList);

        return "menu/list";
    }

    @GetMapping("/regist")
    public String registPage(Model model){
        List<CategoryDto> categoryList = menuService.findCategoryList();
        model.addAttribute("categories", categoryList);

        return "menu/regist";
    }


    @GetMapping("/categories")
    public void categoryList(){
        menuService.findCategoryList();
    }

    @PostMapping("/regist")
    public String registMenu(MenuDto newMenu){
        menuService.registMenu(newMenu);
        return "redirect:/menu/list";
    }

    @GetMapping("/modify")
    public void modifyPage(int code, Model model){
        model.addAttribute("menu", menuService.findMenuByCode(code));
    }

    @PostMapping("/modify")
    public String modifyMenu(MenuDto modifyMenu){
        menuService.modifyMenu(modifyMenu);
        return "redirect:/menu/" + modifyMenu.getMenuCode();
    }

    @GetMapping("/remove")
    public String removeMenu(@RequestParam int code) {
        menuService.removeMenu(code);
        return "redirect:/menu/list";
    }

    @GetMapping("/search")
    public String searchMenu(@RequestParam(required = false) String type,
                             @RequestParam(required = false) String query,
                             Model model) {

        List<MenuDto> menuList = new ArrayList<>();

        try {
            if ("price".equals(type)) {
                if (query == null || query.isBlank()) {
                    model.addAttribute("error", "가격을 입력해주세요.");
                    return "menu/errorPage";
                }

                menuList = menuService.findMenuByMenuPrice(Integer.parseInt(query));
                model.addAttribute("menuList", menuList);
                return "menu/list";

            } else if ("name".equals(type)) {
                // 이름 검색 로직 나중에 추가
            }

        } catch (NumberFormatException e) {
            model.addAttribute("error", "올바른 숫자를 입력해주세요.");
            return "menu/errorPage";
        }

        return "redirect:/menu/list";
    }

}