class PDFConverterCustom < (Asciidoctor::Converter.for 'pdf')
    register_for 'pdf'

    def convert_admonition node
        type = node.attr 'name'
        key_prefix = %(admonition_#{type}_)
        return super if (entries = theme.each_pair.select {|name, val| name.start_with? key_prefix }).empty?
        save_theme do
            entries.each {|name, val| theme[%(admonition_#{name.to_s.delete_prefix key_prefix})] = val }
            super
        end
    end


    def ink_title_page doc
        move_cursor_to page_height * 0.70
        theme_font :title_page_subtitle do
            ink_prose "Licenciatura em Engenharia Informática", align: :center, margin: 0
            move_down 8
            ink_prose "Programação Aplicada", align: :center, margin: 0
        end

        move_cursor_to page_height * 0.50
        doctitle = doc.doctitle partition: true

        theme_font :title_page_title do
            ink_prose doctitle.main, align: :center, color: theme.title_page_title_font_color, line_height: 1.2, margin: 0
        end

        move_cursor_to page_height * 0.30

        theme_font :title_page_authors do
            date_text = "Elaborado em: 2026/06/02"
            ink_prose date_text, align: :center, margin: 0

            move_down 10
            date_text_2 = doc.attr('revdate') || "Atualizado em: 2026/06/02"
            ink_prose date_text_2, align: :center, margin: 0

            move_down 25

            authors_label = "Nome e número do(s) Aluno(s):"
            ink_prose "<strong>#{authors_label}</strong>", align: :center, margin: 0
            move_down 6

            authors_list = doc.authors.map {|author| author.name }.join("<br>")
            ink_prose authors_list, align: :center, margin: 0
        end
    end
end