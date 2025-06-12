package com.podoseee.springdatajpa.menu.service;

import com.podoseee.springdatajpa.menu.dto.CategoryDto;
import com.podoseee.springdatajpa.menu.dto.MenuDto;
import com.podoseee.springdatajpa.menu.entity.Category;
import com.podoseee.springdatajpa.menu.entity.Menu;
import com.podoseee.springdatajpa.menu.repository.CategoryRepository;
import com.podoseee.springdatajpa.menu.repository.MenuRepository;
import com.podoseee.springdatajpa.menu.util.PageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuService {

    private final ModelMapper modelMapper;
    private final MenuRepository menuRepository;
    private final PageUtil pageUtil;
    private final CategoryRepository categoryRepository;

    // 1. findById
    public MenuDto findMenuByCode(int menuCode){

        // findById(식별자) : Optional<T>
        // * Optional : NullPointerException 방지를 위한 다양한 기능 제공

        Menu menu = menuRepository.findById(menuCode)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 메뉴 코드입니다."));

        // Menu 엔티티 => Menu DTO로 변환해서 반환
        // Entity <=> DTO 상호 변환 도와주는 라이브러리 사용 : ModelMapper
        return modelMapper.map(menu, MenuDto.class);
    }

    // 2. findAll
    public List<MenuDto> findMenuList(){
        // 1) findAll() : List<T>
        //List<Menu> menuList = menuRepository.findAll();

        // 2) findAll(Sort) : List<T> - 정렬 기준을 전달해서 실행
        // List<Menu> menuList = menuRepository.findAll(Sort.by("menuCode").descending()); // 정렬기준이 한 개 일 때
        List<Menu> menuList = menuRepository.findAll(Sort.by( // 정렬기준이 여러 개 일 때
                Sort.Order.asc("categoryCode"), // 첫 번째 기준
                Sort.Order.desc("menuPrice")    // 두 번째 기준
        ));

        //         .stream()        .map()           .collect()
        // List<Menu> => Stream<Menu> => Stream<MenuDto> => List<MenuDto>
        return menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDto.class))
                .collect(Collectors.toList());
    }

    public Map<String, Object> findMenuList(Pageable pageable){

        // 3) findAll(Pageable) : Page<T> - 페이지 정보와 해당 요청 페이지에 필요한 엔티티목록조회결과(List<T>)가 담긴 Page 객체 반환
        Page<Menu> pageAndMenu = menuRepository.findAll(pageable);

        /*
        log.info("총 개수: {}", pageAndMenu.getTotalElements());             // totalCount
        log.info("한 페이지당 표현할 개수: {}", pageAndMenu.getSize());      // display
        log.info("총 페이지 수: {}", pageAndMenu.getTotalPages());           // totalPage
        log.info("현재 요청 페이지번호: {}", pageAndMenu.getNumber() + 1);   // page
        log.info("첫 페이지 여부: {}", pageAndMenu.isFirst());
        log.info("마지막 페이지 여부: {}", pageAndMenu.isLast());
        log.info("정렬 방식: {}", pageAndMenu.getSort());
        log.info("요청 페이지에 실제 조회된 개수: {}", pageAndMenu.getNumberOfElements());
        log.info("현재 페이지에 조회된 메뉴 목록: {}", pageAndMenu.getContent());
         */
        Map<String, Object> map = pageUtil.getPageInfo(pageAndMenu, 5);
        map.put("menuList", pageAndMenu.getContent()
                                        .stream()
                                        .map(menu -> modelMapper.map(menu, MenuDto.class))
                                        .toList());

        return map;
    }

    // 3. Native Query 사용
    public List<CategoryDto> findCategoryList(){
        // List<Category> categoryList = categoryRepository.findAll(); // 상위 카테고리 포함 전체 조회
        List<Category> categoryList = categoryRepository.findAllSubCategory();

        // List<Category> => List<CategoryDto>
        return categoryList.stream().map(category -> modelMapper.map(category, CategoryDto.class))
                                    .toList();
    }

    @Transactional
    public void registMenu(MenuDto newMenu){
        menuRepository.save( modelMapper.map(newMenu, Menu.class) );
    }

    @Transactional
    public void modifyMenu(MenuDto modifyMenu){
        // 조회 => setter 이용해서 필드 변경 => commit

        // 조회된 엔티티 => 영속 컨텍스트에 저장 ()
        Menu menu = menuRepository.findById(modifyMenu.getMenuCode())
                                  .orElseThrow(() -> new IllegalArgumentException("잘못된 메뉴 번호입니다."));

        // setter 이용해서 엔티티 필드 변경
        menu.setMenuName(modifyMenu.getMenuName());
        menu.setMenuPrice(modifyMenu.getMenuPrice());
        menu.setOrderableStatus(modifyMenu.getOrderableStatus());
        menu.setCategoryCode(modifyMenu.getCategoryCode());
        // setter에 의해서 변경된 값을 스냅샷과 비교해서
        // 변경감지(dirty checking)되면 update 쿼리가 쓰기 지연 저장소에 저장
        // commit 시점에서 db에 반영

    }

    @Transactional
    public void removeMenu(int code){
        //menuRepository.deleteById(code);
        Menu menu = menuRepository.findById(code)
                                  .orElseThrow(() -> new IllegalArgumentException("잘못된 메뉴 번호입니다."));

        menuRepository.delete(menu);
    }

    public List<MenuDto> findMenuByMenuPrice(int price){

        // 전달된 가격값과 일치하는 메뉴 조회 (WHERE menu_price = xxx)
        // * Native Query + 파라미터 바인딩

        //  * 쿼리메소드
        List<Menu> menuList
                //= menuRepository.findByMenuPriceEquals(price);
                //= menuRepository.findByMenuPriceGreaterThanEqual(price);
                //= menuRepository.findByMenuPriceGreaterThanEqual(price, Sort.by("menuPrice").descending());
                = menuRepository.findByMenuPriceGreaterThanEqualOrderByMenuPriceDesc(price);

        return menuList.stream()
                       .map(menu -> modelMapper.map(menu, MenuDto.class))
                       .toList();

    }

    public List<MenuDto> findMenuByMenuName(String name) {
        // 전달된 메뉴명이 포함된 메뉴 목록 조회
        List<Menu> menuList = menuRepository.findByMenuNameContaining(name);

        return menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDto.class))
                .toList();
    }

    public List<MenuDto> findMenuByPriceAndName(String[] queryArr){
        // 전달된 가격 이상 그리고 메뉴명이 포함
        List<Menu> menuList = menuRepository.findByMenuPriceGreaterThanEqualAndMenuNameContaining(Integer.parseInt(queryArr[0]), queryArr[1]);

        return menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDto.class))
                .toList();
    }

}
