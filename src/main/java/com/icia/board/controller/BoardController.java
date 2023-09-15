package com.icia.board.controller;

import com.icia.board.dto.BoardDTO;
import com.icia.board.dto.BoardFileDTO;
import com.icia.board.dto.CommentDTO;
import com.icia.board.dto.PageDTO;
import com.icia.board.service.BoardService;
import com.icia.board.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/save") // /board/save
    public String saveForm() {
        return "boardPages/boardSave";
    }

    @PostMapping( "/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        boardService.save(boardDTO);
        return "redirect:/board/list";
    }

    // /board/
    // /board?id=1
    // /board/list?page=1
    @GetMapping("/list")
    public String findAll(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                          @RequestParam(value = "q", required = false, defaultValue = "") String q,
                          @RequestParam(value = "type", required = false, defaultValue = "boardTitle") String type,
                          Model model) {
        // 검색이든 아니든 필요한 정보: boardList, paging
        List<BoardDTO> boardDTOList = null;
        PageDTO pageDTO = null;


        // 검색요정인자 아닌지 구분
        if(q.equals("")){
            //일반 페이지 요청
            boardDTOList = boardService.pagingList(page);
            pageDTO = boardService.pageNumber(page);
        }else{
            // 검색 페이지 요청
            boardDTOList = boardService.searchList(q, type, page);
            pageDTO = boardService.searchPageNumber(q, type, page);
        }
        model.addAttribute("boardList", boardDTOList);
        model.addAttribute("paging", pageDTO);
        model.addAttribute("q",q);
        model.addAttribute("type",type);
        model.addAttribute("page",page);
        return "boardPages/boardList";
    }

    @GetMapping("/search")
    public String search(@RequestParam("q") String q,
                         @RequestParam("type") String type,
                         @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                         Model model) {
        List<BoardDTO> boardDTOList = boardService.searchList(q, type, page);
        model.addAttribute("boardList", boardDTOList);

        PageDTO pageDTO = boardService.searchPageNumber(q, type, page);
        model.addAttribute("paging", pageDTO);
        return "boardPages/boardList";
    }

    @GetMapping
    public String findById(@RequestParam("id") Long id,
                           @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                           @RequestParam(value = "q", required = false, defaultValue = "") String q,
                           @RequestParam(value = "type", required = false, defaultValue = "boardTitle") String type,
                           Model model) {
        // 조회수 처리
        // 데이터 가져오기
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        // 첨부된 파일이 있다면 파일을 가져옴
        if (boardDTO.getFileAttached() == 1) {
            List<BoardFileDTO> boardFileDTOList = boardService.findFile(id);
            model.addAttribute("boardFileList", boardFileDTOList);
        }

        List<CommentDTO> commentDTOList = commentService.findAll(id);
        if (commentDTOList.size() == 0) {
            model.addAttribute("commentList", null);
        } else {
            model.addAttribute("commentList", commentDTOList);
        }
        model.addAttribute("q",q);
        model.addAttribute("type",type);
        model.addAttribute("page", page);
        return "boardPages/boardDetail";
    }

    @GetMapping("/update")
    public String updateForm(@RequestParam("id") Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "boardPages/boardUpdate";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        boardService.update(boardDTO);
        BoardDTO dto = boardService.findById(boardDTO.getId());
        model.addAttribute("board", dto);
        return "boardPages/boardDetail";
//        return "redirect:/board?id=" + boardDTO.getId();
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Long id) {
        boardService.delete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/sample")
    public String sampleData() {
        for (int i = 1; i <= 20; i++) {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setBoardWriter("aa");
            boardDTO.setBoardTitle("title" + i);
            boardDTO.setBoardContents("contents" + i);
            boardDTO.setBoardPass("pass" + i);
            boardService.sampleData(boardDTO);
        }
        return "redirect:/board/list";
    }


}










